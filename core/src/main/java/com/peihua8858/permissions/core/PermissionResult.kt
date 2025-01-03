package com.peihua8858.permissions.core

sealed class PermissionResult {
    class PermissionGranted : PermissionResult()
    class PermissionDenied(
        val deniedPermissions: List<String> = emptyList()
    ) : PermissionResult()
}