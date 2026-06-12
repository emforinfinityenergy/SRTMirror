package space.ememememem.srtmirror.ui.player

import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import space.ememememem.srtmirror.player.PlayerManager
import androidx.activity.ComponentActivity

@Composable
fun PlayerScreen(playerManager: PlayerManager) {
    val activity = LocalContext.current as ComponentActivity

    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = playerManager.exoPlayer
                useController = false
            }
        },
        update = { view ->
            view.player = playerManager.exoPlayer
        },
        modifier = Modifier.fillMaxSize()
    )
}
