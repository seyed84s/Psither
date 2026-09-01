package studio.cluvex.aether.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.R
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherCyan
import studio.cluvex.aether.ui.theme.AetherError
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.Navy700
import studio.cluvex.aether.ui.theme.Navy800
import studio.cluvex.aether.ui.theme.Navy900

enum class ButtonMode { IDLE, BUSY, CONNECTED, ERROR }

/**
 * Modern 3D shield connect button with energetic multi-ring scanning animation,
 * glowing neon halos, and crisp glassmorphic surfaces.
 */
@Composable
fun ConnectButton(
    mode: ButtonMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when (mode) {
        ButtonMode.IDLE -> AetherBlue
        ButtonMode.BUSY -> AetherCyan
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
    }

    val contentColor = when (mode) {
        ButtonMode.IDLE -> AetherBlue
        ButtonMode.BUSY -> AetherCyan
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
    }

    val label = when (mode) {
        ButtonMode.IDLE -> "TAP TO CONNECT"
        ButtonMode.BUSY -> "SECURING..."
        ButtonMode.CONNECTED -> "SECURED & ACTIVE"
        ButtonMode.ERROR -> "CONNECTION FAILED"
    }

    val animatedBorder by animateColorAsState(borderColor, tween(500), label = "border")
    val animatedContent by animateColorAsState(contentColor, tween(500), label = "content")

    val transition = rememberInfiniteTransition(label = "btn")

    val pulseAlpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse",
    )

    val innerRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "innerRotation",
    )

    val middleRotation by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "middleRotation",
    )

    val outerRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3800, easing = LinearEasing)),
        label = "outerRotation",
    )

    val borderAlpha = when (mode) {
        ButtonMode.CONNECTED -> pulseAlpha
        ButtonMode.BUSY -> pulseAlpha
        ButtonMode.IDLE -> 0.75f
        ButtonMode.ERROR -> 0.9f
    }

    val glowRadius by animateFloatAsState(
        targetValue = when (mode) {
            ButtonMode.CONNECTED -> 1.25f
            ButtonMode.BUSY -> 1.05f
            else -> 0.85f
        },
        animationSpec = tween(600),
        label = "glow",
    )

    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(40.dp)

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(230.dp)) {
        // Multi-color dynamic ambient radial halo
        Canvas(Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedBorder.copy(alpha = 0.35f * pulseAlpha),
                        AetherCyan.copy(alpha = 0.12f * pulseAlpha),
                        Color.Transparent,
                    ),
                    center = centerOffset,
                    radius = (size.minDimension / 2f) * glowRadius,
                ),
                radius = (size.minDimension / 2f) * glowRadius,
            )
        }

        // Futuristic Multi-Ring Scanning Rings (when BUSY)
        if (mode == ButtonMode.BUSY) {
            // Inner fast cyan ring
            Canvas(Modifier.size(196.dp)) {
                rotate(innerRotation) {
                    drawArc(
                        color = AetherBlue.copy(alpha = 0.95f),
                        startAngle = 30f,
                        sweepAngle = 75f,
                        useCenter = false,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = AetherBlue.copy(alpha = 0.95f),
                        startAngle = 210f,
                        sweepAngle = 75f,
                        useCenter = false,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }

            // Middle counter-rotating purple ring
            Canvas(Modifier.size(210.dp)) {
                rotate(middleRotation) {
                    drawArc(
                        color = AetherCyan,
                        startAngle = 0f,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }

            // Outer segmented neon ring
            Canvas(Modifier.size(224.dp)) {
                rotate(outerRotation) {
                    for (i in 0 until 8) {
                        drawArc(
                            color = AetherBlue.copy(alpha = 0.7f),
                            startAngle = i * 45f + 6f,
                            sweepAngle = 26f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
            }
        }

        // Main button body — modern glassmorphic rounded square with gradient & badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(176.dp)
                .clip(shape)
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            animatedBorder.copy(alpha = borderAlpha),
                            AetherCyan.copy(alpha = borderAlpha * 0.8f),
                            animatedBorder.copy(alpha = borderAlpha),
                        ),
                    ),
                    shape = shape,
                )
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Navy900,
                        ),
                    ),
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick,
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Sleek High-Tech Glowing Power Icon
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Rounded.PowerSettingsNew,
                    contentDescription = "Connect",
                    tint = animatedContent,
                    modifier = Modifier.size(54.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = animatedContent,
                )
            }
        }
    }
}
