package edu.singaporetech.inf2007.team48.project_orion.screens

import android.content.pm.ActivityInfo
import android.hardware.TriggerEvent
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.udp.UdpViewModel
import edu.singaporetech.inf2007.team48.project_orion.components.LockFullScreen
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.GamepadInputEvent
import kotlinx.coroutines.launch


@OptIn(UnstableApi::class)
@Composable
fun RovViewPortScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    udpViewModel: UdpViewModel,
    navController: NavController
) {
    // Locks the screen orientation to landscape, and makes the app full screen
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    LockFullScreen()

    val scope = rememberCoroutineScope()
    /* Normally it would be best to use a view model to manage the player state
    * but since all of these will only be used in this screen, we can just use
    * mutable state to manage the player state.
    */

    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var isSidePanelVisible by remember { mutableStateOf(false) }
    var checklistCheckboxTracker by rememberSaveable { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event -> lifecycle = event }
        lifecycleOwner.lifecycle.addObserver(observer)
        udpViewModel.connect(orionViewModel.rovWifiAssignedIp.value, 5000 )
        //udpViewModel.connect("192.168.10.139", 5000) // Testing with a hardcoded IP
        val xboxInputJob = scope.launch {
            xboxInputViewModel.xboxEvents.collect { event ->
                when (event) {
                    is GamepadInputEvent.ButtonEvent -> {
                        when (event.btn) {
                            KeyEvent.KEYCODE_BUTTON_Y -> {
                                udpViewModel.sendUDPData("Y:1")
                            }

                            KeyEvent.KEYCODE_BUTTON_B -> {
                                udpViewModel.sendUDPData("B:1")
                            }

                            KeyEvent.KEYCODE_BUTTON_X -> {
                                udpViewModel.sendUDPData("X:1")
                            }

                            KeyEvent.KEYCODE_BUTTON_A -> {
                                udpViewModel.sendUDPData("A:1")
                            }
                            KeyEvent.KEYCODE_BUTTON_START -> {
                                isSidePanelVisible = !isSidePanelVisible
                            }
                            KeyEvent.KEYCODE_BUTTON_THUMBL -> {
                                udpViewModel.sendUDPData("T:1")
                            }
                            else -> {
                                Log.d(
                                    "XboxInput_ButtonEvent",
                                    "Button Event: ${event.btn}, Value: ${event.isPressed}"
                                )
                            }

                        }
                    }

                    is GamepadInputEvent.TriggerEvent -> {
                        Log.d(
                            "XboxInput_TriggerEvent",
                            "Trigger Event: ${event.trigger}, Value: ${event.value}"
                        )
                        when (event.trigger) {
                            MotionEvent.AXIS_RTRIGGER -> {
                                udpViewModel.sendUDPData("GAS:${event.value}")
                            }

                            MotionEvent.AXIS_LTRIGGER -> {
                                udpViewModel.sendUDPData("BRK:${event.value}")
                            }
                            else -> {

                            }
                        }
                    }

                    is GamepadInputEvent.JoystickEvent -> {
                        udpViewModel.sendUDPData("J:${event.axis},${event.value}")
                    }

                    is GamepadInputEvent.DPadEvent -> {
                        udpViewModel.sendUDPData("PAD:${event.axis},${event.direction}")
                    }
                }
            }
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            xboxInputJob.cancel()
            udpViewModel.disconnect()
        }
    }

    val rtspMediaSource: MediaSource = RtspMediaSource.Factory()
        .setForceUseRtpTcp(false)
        //.createMediaSource(MediaItem.fromUri("rtsp://192.168.10.139:8554/cam"))
        .createMediaSource(MediaItem.fromUri("rtsp://${orionViewModel.rovWifiAssignedIp.collectAsState().value}:8554/cam"))

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            100, // Min buffer for playback to start
            200, // Max amount of media data to buffer
            100, // Min buffer for playback to start after user action
            100 // Min buffer for playback to restart after rebuffering
        )
        .build()
    val player = ExoPlayer.Builder(LocalContext.current)
        .setLoadControl(loadControl)
        .build()
    player.setMediaSource(rtspMediaSource)
    player.playWhenReady = true
    player.prepare()



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context)
                    .also {
                        it.player = player
                        it.useController = false
                    }
            },
            update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_CREATE -> {
                        it.onResume()
                        it.player?.play()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                        it.player?.play()
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        it.onPause()
                        it.player?.release()

                    }

                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
        {
            FloatingActionButton(
                modifier = Modifier.alpha(
                    // Change the alpha based on the torch state
                    if (isSidePanelVisible) 0.0f else 1f
                ),
                onClick = {
                    // Toggle the state
                    navController.popBackStack()
                },
                containerColor = Color.Red
            ) {
                Icon(
                    // Change the icon based on the torch state
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close and Return to Main Screen"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    modifier = Modifier.alpha(
                        // Change the alpha based on the torch state
                        if (isSidePanelVisible) 0.0f else 1f
                    ),
                    onClick = {
                        // Simulate pressing the Y button on the Xbox controller
                        udpViewModel.sendUDPData("Y:1")
                    },
                    // Modifier and background color...
                ) {
                    Icon(
                        // Change the icon based on the torch state
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Toggle Flashlight"
                    )
                }
                FloatingActionButton(
                    onClick = { isSidePanelVisible = !isSidePanelVisible },
                    // Modifier and background color...
                ) {
                    Icon(
                        // Change the icon based on the torch state
                        imageVector =
                        if (isSidePanelVisible) Icons.Default.Close else
                            Icons.Default.ChecklistRtl,
                        contentDescription = "View Checklists"
                    )
                }
            }
        }
        // Side panel
        AnimatedVisibility(
            visible = isSidePanelVisible,
            enter = slideInHorizontally(
                // Start from the right
                initialOffsetX = { fullWidth -> -fullWidth }
            ),
            exit = slideOutHorizontally(
                // Exit to the right
                targetOffsetX = { fullWidth -> -fullWidth }
            )
        ) {
            // Your LazyColumn or side panel content here
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(400.dp) // Adjust the width as needed
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text =
                        if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_name == "UNDEFINED")
                            "No asset checklist loaded"
                        else
                            "Checklist for asset ${orionViewModel.quickReferenceChecklist.collectAsState().value.asset_name}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                    FloatingActionButton(
                        modifier = Modifier.alpha(
                            // Change the alpha based on the torch state
                            if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_name == "UNDEFINED")
                                0.0f else 1f
                        ),
                        onClick = {
                            val checklistItems =
                                orionViewModel.quickReferenceChecklist.value.asset_checklist
                            val newTracker = checklistCheckboxTracker.toMutableMap()
                            checklistItems.forEachIndexed { index, _ ->
                                newTracker[index] = false
                            }
                            checklistCheckboxTracker =
                                newTracker // Re-assign to trigger recomposition
                        })
                    {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Text("Reset")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Checklist Status"
                            )
                        }
                    }
                }

                LazyColumn {
                    // Assuming orionViewModel.quickReferenceChecklist.value is a list
                    val checklist = orionViewModel.quickReferenceChecklist.value.asset_checklist
                    if (checklist.isNotEmpty()) {
                        items(checklist.size, itemContent = { index ->
                            val item = checklist[index]

                            ChecklistCard(
                                title = item.checklist_title,
                                completed = checklistCheckboxTracker.getOrPut(index) { false }
                            ) { completed ->
                                // Update the checklist item
                                val newTracker = checklistCheckboxTracker.toMutableMap()
                                newTracker[index] = completed
                                checklistCheckboxTracker =
                                    newTracker // Re-assign to trigger recomposition
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistCard(
    title: String,
    completed: Boolean,
    onClick: (Boolean) -> Unit
) {
    val color = if (completed) Color.Green else Color.Unspecified

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Add some padding around the card
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = completed,
            onCheckedChange = { onClick(!completed) },
            colors = CheckboxDefaults.colors(
                checkmarkColor = Color.White,
                checkedColor = color
            )
        )
        Text(
            text = title,
            color = color,
            modifier = Modifier.clickable { onClick(!completed) })
    }
}








