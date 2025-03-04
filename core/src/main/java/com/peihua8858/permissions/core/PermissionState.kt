package com.peihua8858.permissions.core

import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX

interface PermissionState {

    /**
     * The permission to control and observe.
     */
    val permission: String

    /**
     * [permission]'s status
     */
    val status: PermissionStatus

    /**
     * Request the [permission] to the user.
     *
     * This should always be triggered from non-composable scope, for example, from a side-effect
     * or a non-composable callback. Otherwise, this will result in an IllegalStateException.
     *
     * This triggers a system dialog that asks the user to grant or revoke the permission.
     * Note that this dialog might not appear on the screen if the user doesn't want to be asked
     * again or has denied the permission multiple times.
     * This behavior varies depending on the Android level API.
     */
    fun launchPermissionRequest(): Unit
    fun unregister()
    fun refreshPermissionStatus()
    fun setLauncher(launcher: ActivityResultLauncher<String>)
}

fun List<PermissionState>.permissions(): List<String> {
    return map { it.permission }
}

@RestrictTo(LIBRARY_GROUP_PREFIX)
interface MultiplePermissionsState {

    /**
     * List of all permissions to request.
     */
    val permissions: List<PermissionState>

    /**
     * List of permissions revoked by the user.
     */
    val revokedPermissions: List<PermissionState>

    /**
     * When `true`, the user has granted all [permissions].
     */
    val allPermissionsGranted: Boolean

    /**
     * When `true`, the user should be presented with a rationale.
     */
    val shouldShowRationale: Boolean

    /**
     * Request the [permissions] to the user.
     *
     * This should always be triggered from non-composable scope, for example, from a side-effect
     * or a non-composable callback. Otherwise, this will result in an IllegalStateException.
     *
     * This triggers a system dialog that asks the user to grant or revoke the permission.
     * Note that this dialog might not appear on the screen if the user doesn't want to be asked
     * again or has denied the permission multiple times.
     * This behavior varies depending on the Android level API.
     */
    fun launchPermissionRequest()
    fun unregister()
    fun updatePermissionsStatus(permissionsStatus: Map<String, Boolean>)
    fun setLauncher(launcher: ActivityResultLauncher<Array<String>>)
}

val MultiplePermissionsState.deniedPermanently: Boolean
    get() {
        return revokedPermissions.isNotEmpty() && revokedPermissions.all { !it.status.shouldShowRationale }
    }