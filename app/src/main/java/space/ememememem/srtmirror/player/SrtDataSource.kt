package space.ememememem.srtmirror.player

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import java.io.InputStream

@OptIn(UnstableApi::class)
class SrtDataSource(private val inputStream: InputStream) : BaseDataSource(/* isNetwork= */ true) {

    private var uri: Uri? = null

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        transferStarted(dataSpec)
        return C.LENGTH_UNSET.toLong()
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        val bytesRead = inputStream.read(buffer, offset, length)
        if (bytesRead == -1) return C.RESULT_END_OF_INPUT
        bytesTransferred(bytesRead)
        return bytesRead
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        uri = null
        // Closing pipeInput signals the service pump loop to stop writing.
        // It does NOT close the SRT socket — that lives in SrtListenerService.
        try { inputStream.close() } catch (_: Exception) {}
        transferEnded()
    }
}
