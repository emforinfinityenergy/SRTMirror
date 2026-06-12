package space.ememememem.srtmirror.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import space.ememememem.srtmirror.data.AppPreferences

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    onSave: (Int) -> Unit,
    onBack: () -> Unit
) {
    var portText by remember { mutableStateOf(preferences.getPort().toString()) }
    val portValid = portText.toIntOrNull()?.let { it in 1024..65535 } == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(64.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "SRT Listen Port (1024–65535)",
            fontSize = 20.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .background(Color(0xFF333333), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            BasicTextField(
                value = portText,
                onValueChange = { portText = it.filter { c -> c.isDigit() }.take(5) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(Color.Cyan),
                modifier = Modifier.width(200.dp)
            )
        }

        if (!portValid && portText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Port must be between 1024 and 65535",
                fontSize = 14.sp,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Button(
                onClick = {
                    val port = portText.toIntOrNull()
                    if (port != null && port in 1024..65535) {
                        onSave(port)
                    }
                }
            ) {
                Text(text = "Save")
            }
            Button(onClick = onBack) {
                Text(text = "Cancel")
            }
        }
    }
}
