package com.peihua8858.permisstions.fragment

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.peihua8858.permissions.core.MultiplePermissionCallbacks
import com.peihua8858.permissions.core.PermissionCallbacks
import com.peihua8858.permissions.core._requestPermission
import com.peihua8858.permissions.core._requestPermissions

@MainThread
inline fun Fragment.requestPermission(
    permission: String,
    requestBlock: PermissionCallbacks.() -> Unit,
) {
    val permissionCallbacks: PermissionCallbacks =
        PermissionCallbacks().apply { requestBlock() }
    requireActivity(). _requestPermission(permissionCallbacks,permission)
}

@MainThread
inline fun Fragment.requestPermissions(
    vararg permissions: String,
    requestBlock: MultiplePermissionCallbacks.() -> Unit,
) {
    val permissionCallbacks: MultiplePermissionCallbacks =
        MultiplePermissionCallbacks().apply { requestBlock() }
    requireActivity()._requestPermissions(permissionCallbacks,permissions.toList())
}