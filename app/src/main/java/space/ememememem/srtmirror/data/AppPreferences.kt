package space.ememememem.srtmirror.data

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("srt_prefs", Context.MODE_PRIVATE)

    fun getPort(): Int = prefs.getInt("listenPort", 4900)

    fun setPort(port: Int) {
        prefs.edit().putInt("listenPort", port).apply()
    }
}
