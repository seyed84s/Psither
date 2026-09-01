package studio.cluvex.aether.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import studio.cluvex.aether.BuildConfig
import studio.cluvex.aether.R

private const val URL_PORT_GITHUB = "https://github.com/seyed84s/aether-psiphon"
private const val URL_CLUVEX_GITHUB = "https://github.com/CluvexStudio/Aether"
private const val URL_QW_GITHUB = "https://github.com/QW-AI-Code/Aether"
private const val URL_XPSIPHON_GITHUB = "https://github.com/IzumiRain/xPsiphon"

private val PORT_IMPROVEMENTS = listOf(
    "Multi-Country Psiphon Chaining (20+ Regions)",
    "Vibrant, energetic, and highly animated UI",
    "Turbo Scan Mode enabled by default for hyper-fast connections",
    "Smart reconnect, auto-reprovision, and connection stability improvements",
    "Zero-knowledge DNS integration with Psiphon"
)

private val ORIGINAL_FEATURES = listOf(
    "Aether Core Engine (Cloudflare WARP + WireGuard)",
    "xPsiphon routing profiles and node integrations",
)

/**
 * Collapsible "About" card: features the Psither project prominently and
 * acknowledges the base engine (Aether) and routing layer (xPsiphon).
 */
@Composable
fun AboutPanel(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, tween(300), label = "aboutArrow")
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0.0"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        ),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.about_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.about_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(arrowRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.about_version, versionName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.about_core_version, BuildConfig.CORE_VERSION),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(16.dp))

                    // ---- This project (Psither) ----
                    SectionHeader(
                        title = stringResource(R.string.about_port_title),
                        note = stringResource(R.string.about_port_note),
                    )
                    LinkRow(R.drawable.ic_github, "github.com/seyed84s/aether-psiphon", URL_PORT_GITHUB)
                    Spacer(Modifier.height(6.dp))
                    FeatureList(PORT_IMPROVEMENTS)
                    
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(Modifier.height(14.dp))

                    // ---- Acknowledgements ----
                    SectionHeader(
                        title = stringResource(R.string.about_original_title),
                        note = stringResource(R.string.about_original_note),
                    )
                    LinkRow(R.drawable.ic_github, "Cluvex Studio (Aether Core)", URL_CLUVEX_GITHUB)
                    LinkRow(R.drawable.ic_github, "QW-AI-Code (Aether Android)", URL_QW_GITHUB)
                    LinkRow(R.drawable.ic_github, "xPsiphon Project", URL_XPSIPHON_GITHUB)
                    Spacer(Modifier.height(6.dp))
                    FeatureList(ORIGINAL_FEATURES)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, note: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = note,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun LinkRow(iconRes: Int, label: String, url: String) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { runCatching { uriHandler.openUri(url) } }
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun FeatureList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
