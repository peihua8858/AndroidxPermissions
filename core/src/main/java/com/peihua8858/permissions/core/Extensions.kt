package com.peihua8858.permissions.core

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@MainThread
inline fun ComponentActivity.requestPermission(
    permission: String,
    requestBlock: PermissionCallbacksDSL.() -> Unit,
) {
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacksDSL().apply { requestBlock() }
    _requestPermission(
        permissionCallbacks,
        permission
    )
}

fun ComponentActivity._requestPermission(
    callback: PermissionCallbacks,
    permission: String,
) {
    val granted = checkPermission(permission)
    if (granted) {
        onPermissionResult(PermissionResult.PermissionGranted(), callback)
    } else {
        requestMutablePermissionState(permission) {
            val result =
                if (it) PermissionResult.PermissionGranted() else PermissionResult.PermissionDenied(
                    listOf(permission)
                )
            onPermissionResult(result, callback)
        }
    }
}

fun onPermissionResult(result: PermissionResult, callback: PermissionCallbacks) {
    when (result) {
        is PermissionResult.PermissionGranted -> {
            callback.onGranted()
        }

        is PermissionResult.PermissionDenied -> {
            callback.onDenied(result.deniedPermissions)
        }

        is PermissionResult.ShowRational -> {
            callback.onShowRationale(
                result.rationalPermissions,
            )
        }

        is PermissionResult.PermissionDeniedPermanently -> {
            callback.onNeverAskAgain(result.permanentlyDeniedPermissions)
        }
    }
}

@MainThread
inline fun ComponentActivity.requestPermissions(
    vararg permissions: String,
    requestBlock: PermissionCallbacksDSL.() -> Unit,
) {
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacksDSL().apply { requestBlock() }
    _requestPermissions(
        permissionCallbacks,
        permissions.toList()
    )
}

fun ComponentActivity._requestPermissions(
    callback: PermissionCallbacks,
    permissions: List<String>,
) {
    val notGranted = permissions.filter { !checkPermission(it) }
    when {
        notGranted.isEmpty() -> {
            onPermissionResult(PermissionResult.PermissionGranted(), callback)
        }

        else -> {
            requestMutableMultiplePermissionsState(permissions.toList()) { permissionsState ->
                val noGranted = permissionsState.filter { !it.value }
                when {
                    noGranted.isEmpty() ->
                        onPermissionResult(PermissionResult.PermissionGranted(), callback)

                    else -> {
                        onPermissionResult(
                            PermissionResult.PermissionDenied(noGranted.keys.toList()),
                            callback
                        )
                    }
                }
            }
        }
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
    onPermissionResult: (Boolean) -> Unit = {}
): MutablePermissionState {
    val permissionState = MutablePermissionState(permission, this, this)

    // Refresh the permission status when the lifecycle is resumed
    PermissionLifecycleCheckerEffect(permissionState)

    // Remember RequestPermission launcher and assign it to permissionState
    val launcher = launcherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionState.refreshPermissionStatus()
        onPermissionResult(it)
        permissionState.launcher?.unregister()
        permissionState.launcher = null
    }
    permissionState.launcher = launcher
    launcher.launch(permission)
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
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
): MultiplePermissionsState {
    val mutablePermissions = rememberMutablePermissionsState(permissions)
    PermissionsLifecycleCheckerEffect(mutablePermissions)

    val multiplePermissionsState = MutableMultiplePermissionsState(mutablePermissions)

    val launcher = launcherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        multiplePermissionsState.updatePermissionsStatus(permissionsResult)
        onPermissionsResult(permissionsResult)
        multiplePermissionsState.launcher?.unregister()
        multiplePermissionsState.launcher = null
    }
    multiplePermissionsState.launcher = launcher
    launcher.launch(permissions.toTypedArray())
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
