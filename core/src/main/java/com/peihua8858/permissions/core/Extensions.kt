package com.peihua8858.permissions.core

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX

@MainThread
inline fun ComponentActivity.requestPermission(
    permission: String,
    requestBlock: PermissionCallbacks.() -> Unit,
) {
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacks().apply { requestBlock() }
    _requestPermission(
        permissionCallbacks,
        permission
    ).launchPermissionRequest()
}
@RestrictTo(LIBRARY_GROUP_PREFIX)
@MainThread
 fun ComponentActivity._requestPermission(
    callback: PermissionCallbacks,
    permission: String,
):MutablePermissionState {
   return requestMutablePermissionState(permission) {
        onPermissionResult(it, callback)
    }
}

internal fun onPermissionResult(permissionsState: PermissionState, callback: PermissionCallbacks) {
    val permissionStatus = permissionsState.status
    when {
        permissionStatus == PermissionStatus.Granted ->
            callback.onGranted(permissionsState)

        permissionStatus.shouldShowRationale -> {
            callback.onShowRationale(permissionsState)
        }

        else -> {
            callback.onDenied(permissionsState)
        }
    }
}

internal fun onPermissionResult(
    permissionsState: MultiplePermissionsState,
    callback: MultiplePermissionCallbacks
) {
    when {
        permissionsState.allPermissionsGranted ->
            callback.onGranted(permissionsState.permissions)

        permissionsState.shouldShowRationale -> {
            callback.onShowRationale(permissionsState.permissions)
        }

        else -> {
            callback.onDenied(permissionsState.permissions)
        }
    }
}

@MainThread
fun ComponentActivity.requestPermissions(
    vararg permissions: String,
    requestBlock: MultiplePermissionCallbacks.() -> Unit,
) {
    val permissionCallbacks: MultiplePermissionCallbacks =
        MultiplePermissionCallbacks().apply { requestBlock() }
    _requestPermissions(
        permissionCallbacks,
        permissions.toList()
    ).launchMultiplePermissionRequest()
}

@RestrictTo(LIBRARY_GROUP_PREFIX)
@MainThread
 fun ComponentActivity._requestPermissions(
    callback: MultiplePermissionCallbacks,
    permissions: List<String>,
): MultiplePermissionsState {
    return requestMutableMultiplePermissionsState(permissions.toList()) { permissionsState ->
        onPermissionResult(permissionsState, callback)
    }
}


/**
 * Creates a [MutablePermissionState] that is remembered across compositions.
 *
 * It's recommended that apps exercise the permissions workflow as described in the
 * [documentation](https://developer.android.com/training/permissions/requesting#workflow_for_requesting_permissions).
 *
 * @param permission the permission to control and observe.
 * @param onPermissionResult will be called with whether or not the user granted the permission
 *  after [PermissionState.launchPermissionRequest] is called.
 */
internal fun ComponentActivity.requestMutablePermissionState(
    permission: String,
    onPermissionResult: (MutablePermissionState) -> Unit = {}
): MutablePermissionState {
    val permissionState = MutablePermissionState(permission, this, this)

    // Refresh the permission status when the lifecycle is resumed
    PermissionLifecycleCheckerEffect(permissionState)

    // Remember RequestPermission launcher and assign it to permissionState
    val launcher = launcherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.refreshPermissionStatus()
        onPermissionResult(permissionState)
        permissionState.launcher?.unregister()
        permissionState.launcher = null
    }
    permissionState.launcher = launcher
    return permissionState
}

internal fun ComponentActivity.PermissionLifecycleCheckerEffect(
    permissionState: MutablePermissionState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME
) {
    // Check if the permission was granted when the lifecycle is resumed.
    // The user might've gone to the Settings screen and granted the permission.
    val lifecycle = lifecycle
    val permissionCheckerObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == lifecycleEvent) {
                // If the permission is revoked, check again.
                // We don't check if the permission was denied as that triggers a process restart.
                if (permissionState.status != PermissionStatus.Granted) {
                    permissionState.refreshPermissionStatus()
                }
                lifecycle.removeObserver(this)
            }
        }
    }
    lifecycle.addObserver(permissionCheckerObserver)
}

internal fun ComponentActivity.requestMutableMultiplePermissionsState(
    permissions: List<String>,
    onPermissionsResult: (MultiplePermissionsState) -> Unit = {}
): MultiplePermissionsState {
    val mutablePermissions = rememberMutablePermissionsState(permissions)
    PermissionsLifecycleCheckerEffect(mutablePermissions)

    val multiplePermissionsState = MutableMultiplePermissionsState(mutablePermissions)

    val launcher = launcherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        multiplePermissionsState.updatePermissionsStatus(permissionsResult)
        onPermissionsResult(multiplePermissionsState)
        multiplePermissionsState.launcher?.unregister()
        multiplePermissionsState.launcher = null
    }
    multiplePermissionsState.launcher = launcher
    return multiplePermissionsState
}

internal fun ComponentActivity.rememberMutablePermissionsState(
    permissions: List<String>,
): List<MutablePermissionState> {
    // Create list of MutablePermissionState for each permission
    val mutablePermissions: List<MutablePermissionState> =
        permissions.map { MutablePermissionState(it, this, this) }
    //Update each permission with its own launcher
    for (permissionState in mutablePermissions) {
        // Remember launcher and assign it to the permissionState
        val launcher = launcherForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionState.refreshPermissionStatus()
            permissionState.launcher?.unregister()
            permissionState.launcher = null
        }
        permissionState.launcher = launcher
    }
    return mutablePermissions
}

internal fun ComponentActivity.PermissionsLifecycleCheckerEffect(
    permissions: List<MutablePermissionState>,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME
) {
    // Check if the permission was granted when the lifecycle is resumed.
    // The user might've gone to the Settings screen and granted the permission.
    val lifecycle = lifecycle
    val permissionsCheckerObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == lifecycleEvent) {
                for (permission in permissions) {
                    // If the permission is revoked, check again. We don't check if the permission
                    // was denied as that triggers a process restart.
                    if (permission.status != PermissionStatus.Granted) {
                        permission.refreshPermissionStatus()
                    }
                }
                lifecycle.removeObserver(this)
            }
        }

    }
    lifecycle.addObserver(permissionsCheckerObserver)
}

@OptIn(ExperimentalContracts::class)
fun Context.checkPermission(permission: String): Boolean {
    contract {
        returns()
    }
    return ActivityCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

internal fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

val PermissionStatus.isGranted: Boolean
    get() = this == PermissionStatus.Granted

val PermissionStatus.shouldShowRationale: Boolean
    get() = when (this) {
        PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> shouldShowRationale
    }
