package id.renaldi.proximity.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Stable
class AppState(
    val navController: NavHostController,
    val platform: AppPlatformBridge,
) {
    fun navigate(route: AppRoute) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun switchTab(route: AppRoute) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) { saveState = true }
        }
    }

    fun back() {
        navController.popBackStack()
    }
}

@Composable
fun rememberAppState(
    platform: AppPlatformBridge,
    navController: NavHostController = rememberNavController(),
): AppState = remember(navController, platform) { AppState(navController, platform) }
