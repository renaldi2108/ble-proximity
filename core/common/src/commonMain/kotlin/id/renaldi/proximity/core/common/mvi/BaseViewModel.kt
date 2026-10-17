package id.renaldi.proximity.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<F>(capacity = Channel.BUFFERED)
    val effects: Flow<F> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    fun onEvent(event: E) = handleEvent(event)

    protected abstract fun handleEvent(event: E)

    protected fun setState(reducer: S.() -> S) = _state.update(reducer)

    protected fun sendEffect(effect: F) {
        if (_effects.trySend(effect).isSuccess) return
        viewModelScope.launch { _effects.send(effect) }
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(block = block)

    protected fun <T> Flow<T>.collectIntoState(reducer: S.(T) -> S): Job =
        viewModelScope.launch {
            collect { value -> setState { reducer(value) } }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <T> Flow<T>.collectIntoStateWhileSubscribed(reducer: S.(T) -> S): Job {
        val upstream = this
        return viewModelScope.launch {
            _state.subscriptionCount
                .map { it > 0 }
                .distinctUntilChanged()
                .transformLatest { hasSubscribers ->
                    if (!hasSubscribers) delay(SUBSCRIPTION_TIMEOUT_MILLIS)
                    emit(hasSubscribers)
                }
                .distinctUntilChanged()
                .collectLatest { hasSubscribers ->
                    if (!hasSubscribers) return@collectLatest
                    upstream.collect { value -> setState { reducer(value) } }
                }
        }
    }

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L
    }
}
