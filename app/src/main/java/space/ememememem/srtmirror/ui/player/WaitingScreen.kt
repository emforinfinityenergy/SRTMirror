package space.ememememem.srtmirror.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WaitingScreen(
    ip: String,
    port: Int,
    statusText: String,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SRT Mirror",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = statusText,
                fontSize = 28.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (ip.isNotEmpty() && port > 0) {
                Text(
                    text = "Send SRT stream to:",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$ip:$port",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ffmpeg ... -f mpegts \"srt://$ip:$port?mode=caller\"",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }

        Button(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            Text(text = "Settings")
        }
    }
}
