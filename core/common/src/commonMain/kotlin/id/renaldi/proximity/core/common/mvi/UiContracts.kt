package id.renaldi.proximity.core.common.mvi

interface UiState

interface UiEvent

interface UiEffect

data object NoEffect : UiEffect

sealed interface UiMessage : UiEffect {
    val message: String

    data class Info(override val message: String) : UiMessage
    data class Error(override val message: String) : UiMessage
}
