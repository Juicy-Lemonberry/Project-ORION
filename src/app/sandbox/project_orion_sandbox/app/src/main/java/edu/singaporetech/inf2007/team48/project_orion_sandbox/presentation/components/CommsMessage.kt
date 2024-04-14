package edu.singaporetech.inf2007.team48.project_orion_sandbox.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms.BluetoothMessage
import edu.singaporetech.inf2007.team48.project_orion_sandbox.ui.theme.OldRose
import edu.singaporetech.inf2007.team48.project_orion_sandbox.ui.theme.Project_orion_sandboxTheme
import edu.singaporetech.inf2007.team48.project_orion_sandbox.ui.theme.Vanilla

@Composable
fun CommsMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 16.dp else 0.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = if (!message.isFromLocalUser) 16.dp else 0.dp
                )
            )
            .background(
                if (message.isFromLocalUser) OldRose else Vanilla
            )
            .padding(8.dp)
    ) {
        Text(
            text = message.senderName,
            fontSize = 10.sp,
            color = Color.Black
        )
        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}

@Preview
@Composable
fun CommsMessagePreview() {
    Project_orion_sandboxTheme {
        CommsMessage(
            message = BluetoothMessage(
                message = "Mahiru best waifu",
                senderName = "Pixel 6",
                isFromLocalUser = false
            )
        )
    }
}