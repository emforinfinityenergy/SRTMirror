package space.ememememem.srtmirror.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import java.io.InputStream

@OptIn(UnstableApi::class)
class SrtDataSourceFactory(private val inputStream: InputStream) : DataSource.Factory {
    override fun createDataSource(): DataSource = SrtDataSource(inputStream)
}
