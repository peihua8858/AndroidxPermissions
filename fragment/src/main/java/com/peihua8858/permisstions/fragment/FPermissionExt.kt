package com.peihua8858.permisstions.fragment

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.peihua8858.permissions.core.MultiplePermissionCallbacks
import com.peihua8858.permissions.core.PermissionCallbacks
import com.peihua8858.permissions.core.requestPermission
import com.peihua8858.permissions.core.requestPermissions as _requestPermissions

@MainThread
fun Fragment.requestPermission(
    permission: String,
    requestBlock: PermissionCallbacks.() -> Unit,
) {
    requireActivity().requestPermission(permission, requestBlock)
}

@MainThread
fun Fragment.requestPermissions(
    vararg permissions: String,
    requestBlock: MultiplePermissionCallbacks.() -> Unit,
) {
    requireActivity()._requestPermissions(permissions.toList(), requestBlock)
}