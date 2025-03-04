package com.peihua8858.permissions.compose

import androidx.compose.runtime.Composable

@Composable
fun rememberPermissionsState(
    vararg permissions: String,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {}
): MultiplePermissionsState {
    return rememberMutableMultiplePermissionsState(
        permissions.toList(), onPermissionsResult
    )
}