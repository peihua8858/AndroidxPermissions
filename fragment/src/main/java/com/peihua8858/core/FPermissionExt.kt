package com.peihua8858.core

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.peihua8858.permissions.core.PermissionCallbacks
import com.peihua8858.permissions.core._requestPermissions
import com.peihua8858.permissions.core.requestPermission

@MainThread
inline fun Fragment.requestPermission(
    permission: String,
    requestBlock: PermissionCallbacks.() -> Unit,
) {
    requireActivity().requestPermission(permission, requestBlock)
}

@MainThread
inline fun Fragment.requestPermissions(
    vararg permissions: String,
    requestBlock: PermissionCallbacks.() -> Unit,
) {
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacks().apply { requestBlock() }
    requireActivity()._requestPermissions(
        permissionCallbacks,
        permissions.toList()
    )
}