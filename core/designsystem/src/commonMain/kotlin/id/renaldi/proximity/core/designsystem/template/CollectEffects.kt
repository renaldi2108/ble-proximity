package id.renaldi.proximity.core.designsystem.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import id.renaldi.proximity.core.common.mvi.BaseViewModel
import id.renaldi.proximity.core.common.mvi.UiEffect

@Composable
fun <F : UiEffect> CollectEffects(
    viewModel: BaseViewModel<*, *, F>,
    onEffect: suspend (F) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect -> onEffect(effect) }
    }
}
