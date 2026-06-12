package space.ememememem.srtmirror.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import java.io.InputStream

@OptIn(UnstableApi::class)
class PlayerManager(private val context: Context) {

    var exoPlayer: ExoPlayer? = null
        private set

    fun prepare(inputStream: InputStream) {
        release()
        val factory = SrtDataSourceFactory(inputStream)
        val mediaSource = ProgressiveMediaSource.Factory(factory)
            .createMediaSource(MediaItem.fromUri(Uri.parse("srt://stream")))
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaSource(mediaSource)
            playWhenReady = true
            prepare()
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
