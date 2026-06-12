package space.ememememem.srtmirror.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import space.ememememem.srtmirror.data.PlayerState
import space.ememememem.srtmirror.data.SrtViewModel
import space.ememememem.srtmirror.player.PlayerManager
import space.ememememem.srtmirror.ui.player.PlayerScreen
import space.ememememem.srtmirror.ui.player.WaitingScreen
import space.ememememem.srtmirror.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: SrtViewModel,
    playerManager: PlayerManager
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            val state by viewModel.playerState.collectAsState()
            when (val s = state) {
                is PlayerState.Playing -> PlayerScreen(playerManager = playerManager)
                is PlayerState.Waiting -> WaitingScreen(
                    ip = s.ip,
                    port = s.port,
                    statusText = "Waiting for stream...",
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
                is PlayerState.Connecting -> WaitingScreen(
                    ip = s.ip,
                    port = s.port,
                    statusText = "Connecting...",
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
                is PlayerState.Error -> WaitingScreen(
                    ip = "",
                    port = 0,
                    statusText = "Error: ${s.message}",
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                preferences = viewModel.preferences,
                onSave = { port ->
                    viewModel.preferences.setPort(port)
                    viewModel.restartService(port)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
