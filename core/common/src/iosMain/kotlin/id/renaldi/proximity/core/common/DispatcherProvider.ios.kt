package id.renaldi.proximity.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val platformIoDispatcher: CoroutineDispatcher get() = Dispatchers.IO
