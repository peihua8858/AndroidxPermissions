package com.peihua8858.permissions.core

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultLauncher

/**
 * A mutable state object that can be used to control and observe permission status changes.
 *
 * In most cases, this will be created via [requestMutablePermissionState].
 *
 * @param permission the permission to control and observe.
 * @param context to check the status of the [permission].
 * @param activity to check if the user should be presented with a rationale for [permission].
 */
class MutablePermissionState(
    override val permission: String,
    private val context: Context,
    private val activity: Activity
) : PermissionState {

    override var status: PermissionStatus = getPermissionStatus()
        get() = getPermissionStatus()

    override fun launchPermissionRequest() {
        launcher?.launch(
            permission
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    override fun unregister() {
        launcher?.unregister()
        launcher = null
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    override fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    override fun setLauncher(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = context.checkPermission(permission)
        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(activity.shouldShowRationale(permission))
        }
    }
}

sealed interface PermissionStatus {
    data object Granted : PermissionStatus
    data class Denied(
        val shouldShowRationale: Boolean
    ) : PermissionStatus
}

/**
 * A state object that can be hoisted to control and observe multiple permission status changes.
 *
 * In most cases, this will be created via [requestMutableMultiplePermissionsState].
 *
 * @param mutablePermissions list of mutable permissions to control and observe.
 */
internal class MutableMultiplePermissionsState(
    private val mutablePermissions: List<MutablePermissionState>
) : MultiplePermissionsState {

    override val permissions: List<PermissionState> = mutablePermissions

    override val revokedPermissions: List<PermissionState>
        get() = permissions.filter { it.status != PermissionStatus.Granted }

    override val allPermissionsGranted: Boolean
        get() = permissions.all { it.status.isGranted } || // Up to date when the lifecycle is resumed
                revokedPermissions.isEmpty() // Up to date when the user launches the action

    override val shouldShowRationale: Boolean
        get() = permissions.any { it.status.shouldShowRationale } &&
                permissions.none { !it.status.isGranted && !it.status.shouldShowRationale }

    override fun launchPermissionRequest() {
        launcher?.launch(
            permissions.map { it.permission }.toTypedArray()
        ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
    }

    override fun unregister() {
        launcher?.unregister()
        launcher = null
    }

    internal var launcher: ActivityResultLauncher<Array<String>>? = null

    override fun updatePermissionsStatus(permissionsStatus: Map<String, Boolean>) {
        // Update all permissions with the result
        for (permission in permissionsStatus.keys) {
            mutablePermissions.firstOrNull { it.permission == permission }?.apply {
                permissionsStatus[permission]?.let {
                    this.refreshPermissionStatus()
                }
            }
        }
    }

    override fun setLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        this.launcher = launcher
    }
}