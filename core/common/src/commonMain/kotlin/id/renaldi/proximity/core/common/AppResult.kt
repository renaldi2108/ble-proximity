package id.renaldi.proximity.core.common

import kotlinx.coroutines.CancellationException

sealed interface AppResult<out T> {
    data class Success<out T>(val value: T) : AppResult<T>

    data class Failure(val message: String, val cause: Throwable? = null) : AppResult<Nothing>

    val isSuccess: Boolean get() = this is Success
}

suspend fun <T> appRunCatching(message: String, block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Throwable) {
    AppResult.Failure(message = message, cause = error)
}
