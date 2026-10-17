package id.renaldi.proximity.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher

    val confined: CoroutineDispatcher
}

internal expect val platformIoDispatcher: CoroutineDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
object PlatformDispatchers : DispatcherProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val io: CoroutineDispatcher get() = platformIoDispatcher
    override val confined: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1)
}
