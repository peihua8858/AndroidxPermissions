package com.peihua8858.permissions.core

sealed class PermissionResult {
    class PermissionGranted : PermissionResult()
    class PermissionDenied(
            val deniedPermissions: List<String>
    ) : PermissionResult()

    class ShowRational(val rationalPermissions: List<String>) : PermissionResult()
    class PermissionDeniedPermanently(
            val permanentlyDeniedPermissions: List<String>
    ) : PermissionResult()
}