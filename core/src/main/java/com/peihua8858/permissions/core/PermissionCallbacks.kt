package com.peihua8858.permissions.core

abstract class PermissionCallback<T> {
    internal var onGranted: (permissions: T) -> Unit = {}
    internal var onDenied: (permissions: T) -> Unit = {}
    internal var onShowRationale: (permissions: T) -> Unit = {}
    fun onGranted(func: (T) -> Unit) {
        onGranted = func
    }

    fun onDenied(func: (permissions: T) -> Unit) {
        onDenied = func
    }

    fun onShowRationale(func: (permissions: T) -> Unit) {
        onShowRationale = func
    }
}

class PermissionCallbacks : PermissionCallback<PermissionState>()
class MultiplePermissionCallbacks : PermissionCallback<List<PermissionState>>()