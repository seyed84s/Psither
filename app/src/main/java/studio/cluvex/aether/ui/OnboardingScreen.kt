package studio.cluvex.aether.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.R
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherCyan
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.Navy800
import studio.cluvex.aether.ui.theme.Navy900

/**
 * Modern Single-Page Onboarding Screen for Psither.
 * Delivers essential usage recommendations: Direct WARP for filtered social apps,
 * and Psiphon country locations for geo-restricted/sanctioned services.
 */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy900),
    ) {
        // Ambient glow background
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AetherBlue.copy(alpha = 0.15f),
                        AetherCyan.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.2f),
                    radius = size.minDimension * 0.8f,
                ),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AetherCyan.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.8f),
                    radius = size.minDimension * 0.6f,
                ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))

                // Official 3D Shield Logo
                Image(
                    painter = painterResource(R.drawable.ic_psither_logo),
                    contentDescription = "Psither Logo",
                    modifier = Modifier.size(80.dp),
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.onboarding_welcome),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.onboarding_subtitle),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AetherBlue,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                // Card 1: Direct Cloudflare WARP recommendation
                GuideCard(
                    icon = Icons.Rounded.Bolt,
                    iconTint = AetherMint,
                    title = stringResource(R.string.onboarding_direct_title),
                    description = stringResource(R.string.onboarding_direct_desc),
                    borderColor = AetherMint.copy(alpha = 0.4f),
                )

                Spacer(Modifier.height(14.dp))

                // Card 2: Psiphon Multi-Country Chaining
                GuideCard(
                    icon = Icons.Rounded.Public,
                    iconTint = AetherBlue,
                    title = stringResource(R.string.onboarding_psiphon_title),
                    description = stringResource(R.string.onboarding_psiphon_desc),
                    borderColor = AetherBlue.copy(alpha = 0.4f),
                )

                Spacer(Modifier.height(14.dp))

                // Card 3: Security & Privacy
                GuideCard(
                    icon = Icons.Rounded.Security,
                    iconTint = AetherCyan,
                    title = "🔒 No Logs & Smart Zero-Config",
                    description = "Fully automated encryption and smart protocol routing. No accounts, no subscriptions, no tracking.",
                    borderColor = AetherCyan.copy(alpha = 0.3f),
                )

                Spacer(Modifier.height(20.dp))
            }

            // Bottom Action Button
            Button(
                onClick = onFinished,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AetherBlue,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_start),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Composable
private fun GuideCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    borderColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B).copy(alpha = 0.85f),
                        Navy800.copy(alpha = 0.85f),
                    ),
                ),
            )
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFCBD5E1),
                )
            }
        }
    }
}
