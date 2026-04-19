package com.eduardo.retro_relay.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.screens.CadastroScreen
import com.eduardo.retro_relay.screens.ConfigScreen
import com.eduardo.retro_relay.screens.HomeScreen
import com.eduardo.retro_relay.screens.NovoConsoleScreen
import com.eduardo.retro_relay.screens.VoiceControlScreen
import com.eduardo.retro_relay.ui.components.BottomNavigationBar
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainNavigation(viewModel: RelayViewModel, navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                conectado = viewModel.conectado,
                currentRoute = currentRoute,
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCadastroClick = {
                    navController.navigate(Routes.CADASTRO) {
                        popUpTo(Routes.CADASTRO) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onConfigClick = {
                    navController.navigate(Routes.CONFIG) {
                        popUpTo(Routes.HOME)
                        launchSingleTop = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding: PaddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable(Routes.CONFIG) {
                ConfigScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onCadastroClick = {
                        navController.navigate(Routes.CADASTRO) {
                            popUpTo(Routes.CADASTRO) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onConfigClick = {
                        navController.navigate(Routes.CONFIG) {
                            popUpTo(Routes.HOME)
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Routes.CADASTRO) {
                CadastroScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onCanalClick = { index ->
                        navController.navigate(Routes.novoConsoleRoute(index))
                    }
                )
            }

            composable("novo_console/{canal}") { backStackEntry ->
                val canal = backStackEntry.arguments?.getString("canal")?.toIntOrNull() ?: 0
                NovoConsoleScreen(
                    viewModel = viewModel,
                    canal = canal,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.VOICE) {
                VoiceControlScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

        }
    }
}