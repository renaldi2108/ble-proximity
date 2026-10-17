package id.renaldi.proximity.core.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface FlowUseCase<out T> {
    operator fun invoke(): Flow<T>
}
