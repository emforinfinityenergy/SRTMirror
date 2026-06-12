package space.ememememem.srtmirror.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.thibaultbee.srtdroid.core.models.SrtSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import space.ememememem.srtmirror.R
import java.io.IOException
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import kotlin.coroutines.coroutineContext

class SrtListenerService : Service() {

    companion object {
        val stateFlow = MutableStateFlow<PlayerState>(PlayerState.Waiting("", 4900))
        val inputStreamFlow = MutableSharedFlow<InputStream?>(replay = 1)

        private const val TAG = "SrtListenerService"
        private const val CHANNEL_ID = "srt_channel"
        private const val NOTIFICATION_ID = 1
        private const val PIPE_BUFFER_SIZE = 4 * 1024 * 1024 // 4 MB
    }

    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private var listenerJob: Job? = null
    private var serverSocket: SrtSocket? = null
    private var activeClientSocket: SrtSocket? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val port = intent?.getIntExtra("port", 4900) ?: 4900

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SRT Mirror")
            .setContentText("Listening on port $port")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        // Cancel and close everything before starting fresh
        listenerJob?.cancel()
        activeClientSocket?.close()
        serverSocket?.close()

        listenerJob = scope.launch {
            runListenerLoop(port)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        listenerJob?.cancel()
        activeClientSocket?.close()
        serverSocket?.close()
        inputStreamFlow.tryEmit(null)
        supervisorJob.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "SRT Listener", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private suspend fun runListenerLoop(port: Int) {
        val server = SrtSocket()
        serverSocket = server
        try {
            server.bind(InetSocketAddress("0.0.0.0", port))
            server.listen(1)
            stateFlow.value = PlayerState.Waiting(localIp(), port)

            while (coroutineContext.isActive) {
                val clientSocket = server.accept().first
                activeClientSocket = clientSocket
                stateFlow.value = PlayerState.Connecting(localIp(), port)

                handleConnection(clientSocket)

                activeClientSocket = null
                inputStreamFlow.emit(null)
                stateFlow.value = PlayerState.Waiting(localIp(), port)
            }
        } catch (e: Exception) {
            Log.e(TAG, "SRT listener error", e)
            stateFlow.value = PlayerState.Error(e.message ?: "SRT error")
        } finally {
            server.close()
            serverSocket = null
        }
    }

    private suspend fun handleConnection(clientSocket: SrtSocket) {
        // Pipe decouples the SRT socket from ExoPlayer's DataSource lifecycle.
        // Service pumps SRT → pipeOutput; ExoPlayer reads from pipeInput.
        // Closing pipeInput (ExoPlayer done) breaks the pipe on next write,
        // which causes the pump loop to exit and the SRT socket to be closed.
        val pipeOutput = PipedOutputStream()
        val pipeInput = PipedInputStream(pipeOutput, PIPE_BUFFER_SIZE)

        try {
            inputStreamFlow.emit(pipeInput)
            stateFlow.value = PlayerState.Playing

            val srtStream = clientSocket.getInputStream()
            val buf = ByteArray(1316) // max SRT payload size

            try {
                while (coroutineContext.isActive) {
                    val n = srtStream.read(buf)
                    if (n == -1) break   // OBS stopped streaming
                    pipeOutput.write(buf, 0, n)
                }
            } catch (e: IOException) {
                // Broken pipe is expected when ExoPlayer closes pipeInput.
                // Any other IOException is a real SRT read error — log it.
                if (!isBrokenPipe(e)) {
                    Log.w(TAG, "SRT read error", e)
                }
            } finally {
                // Closing pipeOutput signals EOF to ExoPlayer's DataSource.
                try { pipeOutput.close() } catch (_: IOException) {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Connection handling error", e)
        } finally {
            clientSocket.close()
        }
    }

    private fun isBrokenPipe(e: IOException): Boolean {
        val msg = e.message?.lowercase() ?: return false
        return msg.contains("broken pipe") || msg.contains("pipe closed")
    }

    private fun localIp(): String =
        NetworkInterface.getNetworkInterfaces()?.toList()
            ?.flatMap { it.inetAddresses.toList() }
            ?.firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress ?: "0.0.0.0"
}
