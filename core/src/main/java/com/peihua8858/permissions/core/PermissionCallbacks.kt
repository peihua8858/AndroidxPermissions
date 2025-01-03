package com.peihua8858.permissions.core

/**
 */
class PermissionCallbacks {

    private var onGranted: () -> Unit = {}
    private var onDenied: (permissions: List<String>) -> Unit = {}
    fun onGranted(func: () -> Unit) {
        onGranted = func
    }

   fun onDenied(func: (permissions: List<String>) -> Unit) {
        onDenied = func
    }

    internal fun onGranted() {
        onGranted.invoke()
    }

    internal fun onDenied(permissions: List<String>) {
        onDenied.invoke(permissions)
    }

}