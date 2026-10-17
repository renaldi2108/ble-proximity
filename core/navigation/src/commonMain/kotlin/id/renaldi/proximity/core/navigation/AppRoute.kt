package id.renaldi.proximity.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable
    data object Scanner : AppRoute

    @Serializable
    data object History : AppRoute

    @Serializable
    data class Tracker(val address: String) : AppRoute
}
