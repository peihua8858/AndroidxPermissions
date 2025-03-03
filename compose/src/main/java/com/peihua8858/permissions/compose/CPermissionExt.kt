package com.peihua8858.permissions.compose

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.peihua8858.permissions.core.MultiplePermissionCallbacks
import com.peihua8858.permissions.core.MultiplePermissionsState
import com.peihua8858.permissions.core.MutablePermissionState
import com.peihua8858.permissions.core.PermissionCallbacks
import com.peihua8858.permissions.core._requestPermission
import com.peihua8858.permissions.core._requestPermissions

@Composable
inline fun requestPermission(
    permission: String,
    requestBlock: PermissionCallbacks.() -> Unit,
): MutablePermissionState {
    val context= LocalContext.current
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacks().apply { requestBlock() }
    return (context as ComponentActivity)._requestPermission(permissionCallbacks, permission)
}

@Composable
@MainThread
inline fun requestPermissions(
    vararg permissions: String,
    requestBlock: MultiplePermissionCallbacks.() -> Unit,
): MultiplePermissionsState {
    val context= LocalContext.current
    val permissionCallbacks: MultiplePermissionCallbacks =
        MultiplePermissionCallbacks().apply { requestBlock() }
    return (context as ComponentActivity)._requestPermissions(
        permissionCallbacks,
        permissions.toList()
    )
}