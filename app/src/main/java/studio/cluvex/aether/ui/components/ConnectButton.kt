package studio.cluvex.aether.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherError
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.Navy800

enum class ButtonMode { IDLE, BUSY, CONNECTED, ERROR }

@Composable
fun ConnectButton(
    mode: ButtonMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // True Black & Crimson Elite Vibe
    val outerRingColor = when (mode) {
        ButtonMode.IDLE -> AetherError // Crimson ring
        ButtonMode.BUSY -> AetherBlue // Gold when connecting
        ButtonMode.CONNECTED -> AetherMint // Bright Gold
        ButtonMode.ERROR -> AetherError
    }
    
    val innerContentColor = when (mode) {
        ButtonMode.IDLE -> AetherBlue // Gold text
        ButtonMode.BUSY -> AetherMint
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
    }

    val animatedOuter by animateColorAsState(outerRingColor, tween(600), label = "outer")
    val animatedInner by animateColorAsState(innerContentColor, tween(600), label = "inner")

    val transition = rememberInfiniteTransition(label = "connect")
    
    // Smooth spinning for the outer ring when busy
    val sweepRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "sweep",
    )
    
    // Intense pulsing for the halo
    val haloPulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "halo",
    )
    
    val haloScale by animateFloatAsState(
        targetValue = if (mode == ButtonMode.CONNECTED || mode == ButtonMode.BUSY) haloPulse else 1f,
        animationSpec = tween(400),
        label = "haloScale",
    )

    val interaction = remember { MutableInteractionSource() }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(240.dp)) {
        // Outer glowing halo (Crimson or Gold)
        Canvas(modifier = Modifier.size(240.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(animatedOuter.copy(alpha = 0.40f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension / 2f * haloScale,
                ),
                radius = size.minDimension / 2f * haloScale,
            )
            
            // Sharp outer ring
            drawCircle(
                color = animatedOuter.copy(alpha = 0.8f),
                radius = (size.minDimension / 2f) * 0.75f,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // If busy, draw a spinner arc on the ring
            if (mode == ButtonMode.BUSY) {
                drawArc(
                    color = Color.White,
                    startAngle = sweepRotation,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(size.width * 0.125f, size.height * 0.125f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.75f, size.height * 0.75f)
                )
            }
        }

        // Inner solid disc
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Navy800, Color.Black)
                    )
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick,
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Stylized P logo text
                Text(
                    text = "P",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = animatedInner,
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val label = when (mode) {
                    ButtonMode.IDLE -> "CONNECT"
                    ButtonMode.BUSY -> "SECURING..."
                    ButtonMode.CONNECTED -> "CONNECTED"
                    ButtonMode.ERROR -> "RETRY"
                }
                
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = animatedInner,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
