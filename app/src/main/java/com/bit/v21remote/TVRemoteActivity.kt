package com.bit.v21remote

import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TVRemoteActivity : ComponentActivity() {

    private var irManager: ConsumerIrManager? = null
    private var vibrator: Vibrator? = null
    private var lastSendTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        irManager = getSystemService(ConsumerIrManager::class.java)
        vibrator = getSystemService(Vibrator::class.java)

        setContent {
            MaterialTheme {
                TVRemoteScreen(
                    onBackPressed = { finish() },
                    onSendIR = { pattern -> sendIR(pattern) }
                )
            }
        }
    }

    private fun sendIR(pattern: IntArray) {
        val now = System.currentTimeMillis()
        if (now - lastSendTime < 200) return
        lastSendTime = now
        vibrate()
        irManager?.takeIf { it.hasIrEmitter() }?.transmit(38000, pattern)
    }

    private fun vibrate() {
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(50)
            }
        }
    }
}

// Color Palette for Dark Theme Remote UI
private val DarkBg = Color(0xFF121418)
private val CardBg = Color(0xFF22242B)
private val CardBorder = Color(0xFF2E323D)
private val CenterOkBg = Color(0xFF1A1C23)
private val IconColor = Color(0xFFE2E8F0)
private val TextColor = Color(0xFFF8FAFC)
private val PowerColor = Color(0xFF22C55E)
private val DotColor = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVRemoteScreen(
    onBackPressed: () -> Unit,
    onSendIR: (IntArray) -> Unit
) {
    var showNumpadSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header Bar
        HeaderBar(
            onBackPressed = onBackPressed,
            onSendIR = onSendIR
        )

        // Remote Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row 1 of Circular Buttons (Power, Input Source, Menu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_power,
                    tint = PowerColor,
                    contentDescription = "Power",
                    onClick = { onSendIR(IRPatterns.TV_POWER) }
                )
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_input,
                    tint = IconColor,
                    contentDescription = "Input Source",
                    onClick = { onSendIR(IRPatterns.TV_SOURCE) }
                )
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_menu,
                    tint = IconColor,
                    contentDescription = "Menu",
                    onClick = { onSendIR(IRPatterns.TV_MENU) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row 2 of Circular Buttons (Home, Info Numpad, Return)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_home,
                    tint = IconColor,
                    contentDescription = "Home",
                    onClick = { onSendIR(IRPatterns.TV_HOME) }
                )
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_info_one,
                    tint = IconColor,
                    contentDescription = "Info Numpad",
                    onClick = { showNumpadSheet = true }
                )
                RemoteCircleButton(
                    iconRes = R.drawable.ic_remote_return,
                    tint = IconColor,
                    contentDescription = "Return Back",
                    onClick = { onSendIR(IRPatterns.TV_EXIT) }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Circular D-Pad Container
            CircularDpad(onSendIR = onSendIR)

            Spacer(modifier = Modifier.height(36.dp))

            // Bottom Section: Channel & Volume Rockers
            ChannelVolumeSection(onSendIR = onSendIR)
        }
    }

    // Numpad Modal Bottom Sheet
    if (showNumpadSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNumpadSheet = false },
            containerColor = Color(0xFF1B1D24),
            scrimColor = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            NumpadSheetContent(
                onSendIR = onSendIR,
                onClose = { showNumpadSheet = false }
            )
        }
    }
}

@Composable
fun NumpadSheetContent(
    onSendIR: (IntArray) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Numpad Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Number Pad",
                color = TextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Text(
                    text = "✕",
                    color = TextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Numpad 3x3 Grid (1 to 9)
        val numberRows = listOf(
            listOf("1" to IRPatterns.TV_1, "2" to IRPatterns.TV_2, "3" to IRPatterns.TV_3),
            listOf("4" to IRPatterns.TV_4, "5" to IRPatterns.TV_5, "6" to IRPatterns.TV_6),
            listOf("7" to IRPatterns.TV_7, "8" to IRPatterns.TV_8, "9" to IRPatterns.TV_9)
        )

        for (row in numberRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for ((label, signal) in row) {
                    NumpadKeyButton(
                        text = label,
                        onClick = { onSendIR(signal) }
                    )
                }
            }
        }

        // Bottom Row for 'APP', '0', and 'PIC'
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadKeyButton(
                text = "APP",
                fontSize = 14.sp,
                onClick = { onSendIR(IRPatterns.TV_APP) }
            )
            NumpadKeyButton(
                text = "0",
                onClick = { onSendIR(IRPatterns.TV_0) }
            )
            NumpadKeyButton(
                text = "PIC",
                fontSize = 14.sp,
                onClick = { onSendIR(IRPatterns.TV_PIC_MODE) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Streaming & Mode Shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShortcutPillButton("YouTube") { onSendIR(IRPatterns.TV_YOUTUBE) }
            ShortcutPillButton("Netflix") { onSendIR(IRPatterns.TV_NETFLIX) }
            ShortcutPillButton("Hotstar") { onSendIR(IRPatterns.TV_HOTSTAR) }
            ShortcutPillButton("Sound") { onSendIR(IRPatterns.TV_SOUND_MODE) }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun NumpadKeyButton(
    text: String,
    fontSize: TextUnit = 22.sp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = CardBg,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 3.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = TextColor,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ShortcutPillButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                color = TextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HeaderBar(
    onBackPressed: () -> Unit,
    onSendIR: (IntArray) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_header_back),
                contentDescription = "Back",
                tint = IconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Samsung TV",
            color = TextColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onSendIR(IRPatterns.TV_CAST) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_header_cast),
                    contentDescription = "Cast",
                    tint = IconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = { onSendIR(IRPatterns.TV_RATIO) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_header_expand),
                    contentDescription = "Expand",
                    tint = IconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = { onSendIR(IRPatterns.TV_SETTING) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_header_more_vert),
                    contentDescription = "More Options",
                    tint = IconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun RemoteCircleButton(
    iconRes: Int,
    tint: Color,
    contentDescription: String,
    size: Dp = 66.dp,
    iconSize: Dp = 26.dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = CardBg,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun CircularDpad(onSendIR: (IntArray) -> Unit) {
    Surface(
        modifier = Modifier.size(250.dp),
        shape = CircleShape,
        color = CardBg,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 3.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // UP Touch Zone
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.38f)
                    .clickable { onSendIR(IRPatterns.TV_UP) }
            )

            // TOP DOT
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
                    .size(6.dp)
                    .background(DotColor, CircleShape)
            )

            // DOWN Touch Zone
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.38f)
                    .clickable { onSendIR(IRPatterns.TV_DOWN) }
            )

            // BOTTOM DOT
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(6.dp)
                    .background(DotColor, CircleShape)
            )

            // LEFT Touch Zone
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(0.38f)
                    .clickable { onSendIR(IRPatterns.TV_LEFT) }
            )

            // LEFT DOT
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 32.dp)
                    .size(6.dp)
                    .background(DotColor, CircleShape)
            )

            // RIGHT Touch Zone
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.38f)
                    .clickable { onSendIR(IRPatterns.TV_RIGHT) }
            )

            // RIGHT DOT
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 32.dp)
                    .size(6.dp)
                    .background(DotColor, CircleShape)
            )

            // Center OK Button
            Surface(
                modifier = Modifier
                    .size(92.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .clickable { onSendIR(IRPatterns.TV_OK) },
                shape = CircleShape,
                color = CenterOkBg,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "OK",
                        color = TextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelVolumeSection(onSendIR: (IntArray) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left Side: Channel Control
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(72.dp)
                    .height(164.dp),
                shape = RoundedCornerShape(36.dp),
                color = CardBg,
                border = BorderStroke(1.dp, CardBorder),
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // CH UP
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onSendIR(IRPatterns.TV_CH_PLUS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_up),
                            contentDescription = "Channel Up",
                            tint = IconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // CH TEXT
                    Text(
                        text = "CH",
                        color = TextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // CH DOWN
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onSendIR(IRPatterns.TV_CH_MINUS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_down),
                            contentDescription = "Channel Down",
                            tint = IconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-button below Channel: More Options
            RemoteCircleButton(
                iconRes = R.drawable.ic_more_horiz,
                tint = IconColor,
                contentDescription = "More Options",
                size = 60.dp,
                iconSize = 24.dp,
                onClick = { onSendIR(IRPatterns.TV_SETTING) }
            )
        }

        // Right Side: Volume Control
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(72.dp)
                    .height(164.dp),
                shape = RoundedCornerShape(36.dp),
                color = CardBg,
                border = BorderStroke(1.dp, CardBorder),
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // VOL UP
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onSendIR(IRPatterns.TV_VOL_PLUS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = "Volume Up",
                            tint = IconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // VOL TEXT
                    Text(
                        text = "VOL",
                        color = TextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // VOL DOWN
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { onSendIR(IRPatterns.TV_VOL_MINUS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_minus),
                            contentDescription = "Volume Down",
                            tint = IconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-button below Volume: Mute
            RemoteCircleButton(
                iconRes = R.drawable.ic_volume_off,
                tint = IconColor,
                contentDescription = "Mute",
                size = 60.dp,
                iconSize = 24.dp,
                onClick = { onSendIR(IRPatterns.TV_MUTE) }
            )
        }
    }
}

// IRPatterns object containing all 34 IR Signal frequency patterns from TVRemoteActivity.java
object IRPatterns {
    val TV_POWER = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,600,500,600,1650,600,550,550,550,600,500,600,550,600,500,600,550,550,1700,550,550,600,1650,600,1650,550,1700,550,1650,600,1650,600,1650,600)
    val TV_MUTE = intArrayOf(4550,4400,650,1600,650,1600,650,1600,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,1650,600)
    val TV_MENU = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,500,600,1650,600,550,550,1700,550,1700,550,1650,600,550,550,550,600,1650,600,550,550,1650,600)
    val TV_EXIT = intArrayOf(4550,4350,650,1600,650,1650,600,1650,550,550,600,500,600,550,600,500,600,550,550,1700,550,1700,550,1650,600,550,550,550,600,550,550,550,600,1650,600,550,600,1650,550,1700,550,550,600,1650,550,550,600,550,550,550,600,1650,600,500,600,550,600,1650,550,550,600,1650,600,1650,600)
    val TV_LEFT = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,550,1700,550,550,600,1650,600,500,600,550,550,1700,550,1650,600,550,600,500,600,1650,600,550,550,1700,550,1650,600,550,550,550,600,1650,600)
    val TV_RIGHT = intArrayOf(250,800,4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,500,600,1650,600,550,550,550,600,550,550,1650,600,1650,600,550,550,1700,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,1650,600)
    val TV_UP = intArrayOf(4550,4400,650,1600,650,1600,650,1600,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,500,600,1650,600,1650,600,1650,600,1650,600,1650,550,550,600,550,550,1650,600)
    val TV_DOWN = intArrayOf(4550,4400,600,1600,650,1600,650,1650,550,550,600,500,600,550,600,500,600,550,550,1700,550,1700,550,1650,600,550,550,550,600,550,550,550,600,550,550,1650,600,550,550,550,600,550,550,550,600,1650,600,1650,600,500,600,550,550,1700,550,1650,600,1650,600,1650,600,550,550,550,600,1650,600)
    val TV_OK = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,500,600,550,550,550,600,1650,600,550,550,1650,600,1650,600,550,550,1700,550,1650,600,1650,600,550,550,1700,550,550,600,500,600,1650,600)
    val TV_VOL_PLUS = intArrayOf(4600,4350,650,1600,650,1650,550,1650,600,550,600,500,600,550,550,550,600,550,550,1700,550,1650,600,1650,600,550,550,550,600,550,550,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,600,500,600,550,550,550,600,550,550,550,600,500,600,1650,600,1650,600,1650,600,1650,600,1650,550)
    val TV_VOL_MINUS = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,550,550,550,600,1650,600,1650,550,550,600,1650,600,500,600,550,600,500,600,550,550,550,600,550,550,1650,600,550,600,1650,550,1700,550,1650,600,1650,600)
    val TV_CH_PLUS = intArrayOf(4600,4350,650,1600,650,1600,650,1600,600,550,600,500,600,550,550,550,600,550,550,1650,600,1650,600,1650,600,550,550,550,600,500,600,550,550,1650,600,550,600,500,600,550,550,1700,550,550,600,550,550,1650,600,550,600,500,600,550,550,1700,550,550,600,1650,550,1700,550,550,600,1650,600,1650,600,1650,550)
    val TV_CH_MINUS = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,500,600,550,600,500,600,550,600,1650,550,550,600,550,550,550,600,1650,550,1700,550,1700,550,1650,600,550,600,1650,550,1700,550,1650,600)
    val TV_SETTING = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,1650,600,1650,550,550,600,1650,600,500,600,550,600,1650,550,550,600,550,550,550,600,1650,600,500,600,1650,600,1650,600,550,550,1700,550)
    val TV_SOURCE = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,1650,600,500,600,550,550,550,600,550,550,550,600,500,600,1650,600,1650,600,1650,600,1650,600,1650,550,1700,550,1650,600)
    val TV_HOME = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,1650,600,500,600,550,550,1700,550,1700,550,1650,600,1650,600,550,550,550,600,1650,600,1650,600,500,600,550,550,550,600,1650,600)
    val TV_PIC_MODE = intArrayOf(4600,4350,650,1600,650,1650,550,1650,600,550,600,500,600,550,550,550,600,550,550,1700,550,1650,600,1650,600,550,550,550,600,550,550,550,600,500,600,550,600,1650,550,550,600,1650,600,1650,600,500,600,550,550,550,600,1650,600,550,550,1650,600,550,600,500,600,1650,600,1650,600,1650,600)
    val TV_SOUND_MODE = intArrayOf(4500,4450,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,1650,550,1700,550,1650,600,1650,600,550,550,550,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,500,600,1650,600)
    val TV_YOUTUBE = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,550,550,550,600,1650,550,1700,550,1700,550,1650,600,1650,600,550,550,550,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,500,600)
    val TV_HOTSTAR = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,500,600,1650,600,1650,600,550,550,550,600,550,550,1650,600,1650,600,1650,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600)
    val TV_NETFLIX = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,1650,600,1650,550,550,600,550,550,1650,600,1650,600,1650,600,550,550,550,600,500,600,1650,600,1650,600,550,550,550,600,500,600,1650,600)
    val TV_APP = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,1700,550,1650,600,550,550,550,600,1650,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600)
    val TV_1 = intArrayOf(4550,4400,600,1600,650,1650,600,1650,550,550,600,500,600,550,600,500,600,550,600,1650,550,1700,550,1650,600,550,600,500,600,550,550,550,600,550,550,550,600,550,550,1650,600,550,550,550,600,550,550,550,600,550,550,550,600,1650,600,1650,600,550,550,1700,550,1650,600,1650,600,1650,600)
    val TV_2 = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,550,1700,550,550,600,1650,600,500,600,550,600,500,600,550,550,550,600,550,550,1650,600,550,600,1650,550,1700,550,1650,600,1650,600)
    val TV_3 = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,550,550,550,600,1650,600,1650,550,1700,550,1650,600)
    val TV_4 = intArrayOf(4550,4400,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,1650,550,1700,550,1650,600,550,600,500,600,550,550,550,600,550,550,550,600,550,550,550,600,1650,600,500,600,550,550,550,600,550,550,1650,600,1650,600,1650,600,550,550,1650,600,1650,600,1650,600,1650,600)
    val TV_5 = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,550,550,550,600,1650,600,1650,600,1650,550,550,600,550,550,550,600,550,550,550,600,1650,600,500,600,550,550,1700,550,550,600,500,600,550,600,500,600,550,600,1650,550,1700,550,550,600,1650,600,1650,550,1700,550,1650,600)
    val TV_6 = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,500,600,550,600,500,600,500,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,550,550,1650,600,550,550,1700,550,550,600,550,550,550,600,500,600,1650,600,550,550,1700,550,550,600,1650,600,1650,550,1700,550,1650,600)
    val TV_7 = intArrayOf(4550,4400,650,1600,650,1600,600,1650,600,550,550,550,600,550,550,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,600,500,600,550,550,550,600,550,550,1650,600,1650,600,550,550,550,600,1650,600,1650,600,1650,550,1700,550)
    val TV_8 = intArrayOf(4550,4400,600,1600,650,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,1650,550,1700,550,1650,600,550,600,500,600,550,550,550,600,550,550,1700,550,550,600,1650,550,1700,550,550,600,550,550,550,600,500,600,550,600,1650,550,550,600,550,550,1650,600,1650,600,1650,600,1650,600)
    val TV_9 = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,550,550,550,600,500,600,550,600,500,600,1650,600,1650,600,1650,600,500,600,550,550,550,600,550,550,1700,550,550,600,500,600,550,600,1650,550,1700,550,1650,600,1650,600)
    val TV_0 = intArrayOf(4550,4400,600,1600,650,1650,600,1650,550,550,600,550,550,550,600,500,600,550,600,1650,550,1700,550,1650,600,550,600,500,600,550,550,550,600,1650,600,550,600,500,600,550,550,1700,550,550,600,550,550,550,600,500,600,1650,600,1650,600,1650,600,500,600,1650,600,1650,600,1650,600,1650,600)
    val TV_CAST = intArrayOf(4550,4400,650,1600,600,1650,600,1650,600,500,600,550,550,550,600,550,550,550,600,1650,600,1650,550,1700,550,550,600,500,600,550,600,500,600,550,550,1700,550,1650,600,1650,600,1650,600,500,600,550,600,1650,550,550,600,550,550,550,600,550,550,550,600,1650,600,1650,550,550,600)
    val TV_RATIO = intArrayOf(4550,4400,650,1600,650,1600,650,1650,600,500,600,550,550,550,600,550,550,550,600,1650,550,1700,550,1700,550,550,600,500,600,550,600,500,600,550,550,1700,550,1650,600,550,600,1650,550,550,600,1650,600,1650,550,550,600,550,550,550,600,1650,600,500,600,1650,600,550,550,550,600,1650,600)
}

// Android Studio Compose Previews
@Preview(name = "Main TV Remote UI", showBackground = true, heightDp = 800)
@Composable
fun TVRemoteScreenPreview() {
    MaterialTheme {
        TVRemoteScreen(
            onBackPressed = {},
            onSendIR = {}
        )
    }
}

@Preview(name = "Numpad Sheet Content UI", showBackground = true)
@Composable
fun NumpadSheetPreview() {
    MaterialTheme {
        Surface(color = DarkBg) {
            NumpadSheetContent(
                onSendIR = {},
                onClose = {}
            )
        }
    }
}