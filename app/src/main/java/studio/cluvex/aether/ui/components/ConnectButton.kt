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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherError
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.Navy700

enum class ButtonMode { IDLE, BUSY, CONNECTED, ERROR }

/**
 * Stealth Elite connect button — a large rounded square with an animated gold/crimson
 * border, a glowing "P" logo in the centre, and a circular spinner overlay when busy.
 */
@Composable
fun ConnectButton(
    mode: ButtonMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when (mode) {
        ButtonMode.IDLE -> AetherBlue
        ButtonMode.BUSY -> AetherBlue
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
    }

    val contentColor = when (mode) {
        ButtonMode.IDLE -> AetherBlue
        ButtonMode.BUSY -> AetherBlue
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
    }

    val label = when (mode) {
        ButtonMode.IDLE -> "CONNECT"
        ButtonMode.BUSY -> "SECURING"
        ButtonMode.CONNECTED -> "SECURED"
        ButtonMode.ERROR -> "RETRY"
    }

    val animatedBorder by animateColorAsState(borderColor, tween(500), label = "border")
    val animatedContent by animateColorAsState(contentColor, tween(500), label = "content")

    val transition = rememberInfiniteTransition(label = "btn")

    val pulseAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse",
    )

    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "rotation",
    )

    val borderAlpha = when (mode) {
        ButtonMode.CONNECTED -> pulseAlpha
        ButtonMode.BUSY -> pulseAlpha
        ButtonMode.IDLE -> 0.45f
        ButtonMode.ERROR -> 0.85f
    }

    val glowRadius by animateFloatAsState(
        targetValue = when (mode) {
            ButtonMode.CONNECTED -> 1.15f
            ButtonMode.BUSY -> 0.95f
            else -> 0f
        },
        animationSpec = tween(600),
        label = "glow",
    )

    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(36.dp)

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(210.dp)) {
        // Outer glow
        if (glowRadius > 0f) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedBorder.copy(alpha = 0.28f * pulseAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.minDimension / 2f * glowRadius,
                    ),
                    radius = size.minDimension / 2f * glowRadius,
                )
            }
        }

        // Busy spinner ring (circular, just outside the square)
        if (mode == ButtonMode.BUSY) {
            Canvas(Modifier.size(190.dp)) {
                rotate(rotationAngle) {
                    drawArc(
                        color = animatedBorder,
                        startAngle = 0f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }
        }

        // Main button body — rounded square
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .clip(shape)
                .border(
                    width = 2.5.dp,
                    color = animatedBorder.copy(alpha = borderAlpha),
                    shape = shape,
                )
                .background(
                    Brush.radialGradient(listOf(Navy700, Color.Black)),
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
                Text(
                    text = "P",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = animatedContent,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = animatedContent.copy(alpha = 0.75f),
                )
            }
        }
    }
}
