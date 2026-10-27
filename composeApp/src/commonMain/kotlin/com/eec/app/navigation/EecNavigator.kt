package com.eec.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class EecNavigator(val controller: NavHostController) {
    fun select(screen: Screen) {
        controller.navigate(screen.route) {
            popUpTo(controller.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun push(screen: Screen) {
        controller.navigate(screen.route) { launchSingleTop = true }
    }

    fun pop(): Boolean = controller.navigateUp()
}

@Composable
fun rememberEecNavigator(): EecNavigator {
    val controller = rememberNavController()
    return remember(controller) { EecNavigator(controller) }
}
