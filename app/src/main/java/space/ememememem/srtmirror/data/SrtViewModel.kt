package space.ememememem.srtmirror.data

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream

class SrtViewModel(application: Application) : AndroidViewModel(application) {

    val playerState: StateFlow<PlayerState> = SrtListenerService.stateFlow
    val inputStreamFlow: SharedFlow<InputStream?> = SrtListenerService.inputStreamFlow

    val preferences = AppPreferences(application)

    fun startService(port: Int) {
        val intent = Intent(getApplication(), SrtListenerService::class.java)
            .putExtra("port", port)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplication<Application>().startForegroundService(intent)
        } else {
            getApplication<Application>().startService(intent)
        }
    }

    fun stopService() {
        getApplication<Application>().stopService(
            Intent(getApplication(), SrtListenerService::class.java)
        )
    }

    fun restartService(port: Int) {
        stopService()
        startService(port)
    }
}
