package com.peihua8858.permissions.core

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import java.util.UUID

internal fun <I, O> ComponentActivity.launcherForActivityResult(
    contract: ActivityResultContract<I, O>,
    onResult: (O) -> Unit
): ManagedActivityResultLauncher<I, O> {
    // It doesn't really matter what the key is, just that it is unique
    // and consistent across configuration changes
    val key = UUID.randomUUID().toString()

    val realLauncher = ActivityResultLauncherHolder<I>()
    val returnedLauncher = ManagedActivityResultLauncher(realLauncher, contract)

    realLauncher.launcher = activityResultRegistry.register(key, contract) {
        onResult(it)
    }
    // DisposableEffect ensures that we only register once
    // and that we unregister when the composable is disposed
    return returnedLauncher
}


/**
 * A launcher for a previously-[prepared call][ActivityResultCaller.registerForActivityResult]
 * to start the process of executing an [ActivityResultContract].
 *
 * This launcher does not support the [unregister] function. Attempting to use [unregister] will
 * result in an [IllegalStateException].
 *
 * @param I type of the input required to launch
 */
internal class ManagedActivityResultLauncher<I, O> internal constructor(
    private val launcher: ActivityResultLauncherHolder<I>,
    private val currentContract: ActivityResultContract<I, O>
) : ActivityResultLauncher<I>() {

    /**
     * This function should never be called and doing so will result in an
     * [UnsupportedOperationException].
     *
     * @throws UnsupportedOperationException if this function is called.
     */
    override fun unregister() {
        launcher.unregister()
    }

    override fun getContract(): ActivityResultContract<I, *> {
        return currentContract
    }

    override fun launch(input: I, options: ActivityOptionsCompat?) {
        launcher.launch(input, options)
    }
}

internal class ActivityResultLauncherHolder<I> {
    var launcher: ActivityResultLauncher<I>? = null

    fun launch(input: I, options: ActivityOptionsCompat?) {
        launcher?.launch(input, options) ?: error("Launcher has not been initialized")
    }

    fun unregister() {
        launcher?.unregister() ?: error("Launcher has not been initialized")
    }
}