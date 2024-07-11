package com.avd.powertrack.router

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avd.powertrack.screens.MainScreen
import com.avd.powertrack.screens.SettingsScreen

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object SettingsScreen : Screen("settings_screen")
}


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navHost = NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
            composable(
                route = Screen.MainScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(
                            durationMillis = 500
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(
                            durationMillis = 500
                        )
                    )
                }
            ) {
                MainScreen(navController)
            }
            composable(
                route = Screen.SettingsScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(
                            durationMillis = 500
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(
                            durationMillis = 500
                        )
                    )
                }


                ) {
                SettingsScreen(navController)
            }
        }


}


