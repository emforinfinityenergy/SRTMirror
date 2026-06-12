package space.ememememem.srtmirror

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import space.ememememem.srtmirror.data.SrtViewModel
import space.ememememem.srtmirror.player.PlayerManager
import space.ememememem.srtmirror.ui.navigation.AppNavHost
import space.ememememem.srtmirror.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SrtViewModel by viewModels()

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val port = viewModel.preferences.getPort()
        viewModel.startService(port)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val playerManager = remember { PlayerManager(this@MainActivity) }

                DisposableEffect(Unit) {
                    onDispose { playerManager.release() }
                }

                LaunchedEffect(Unit) {
                    viewModel.inputStreamFlow.collect { stream ->
                        if (stream != null) {
                            playerManager.prepare(stream)
                        } else {
                            playerManager.release()
                        }
                    }
                }

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    playerManager = playerManager
                )
            }
        }
    }

    override fun onDestroy() {
        viewModel.stopService()
        super.onDestroy()
    }
}
