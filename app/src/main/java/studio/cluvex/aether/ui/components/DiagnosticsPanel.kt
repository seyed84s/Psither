package studio.cluvex.aether.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.collectAsState
import android.widget.Toast
import kotlinx.coroutines.launch
import studio.cluvex.aether.R
import studio.cluvex.aether.core.CheckState
import studio.cluvex.aether.core.ComponentCheck
import studio.cluvex.aether.core.Diagnostics
import studio.cluvex.aether.core.DiagnosticsLog
import studio.cluvex.aether.core.LogLevel
import studio.cluvex.aether.core.LogLine

/**
 * A collapsible “pro” panel that shows the live status of every part of the
 * tunnel plus a scrollable, copyable technical log. This is what tells the user
 * exactly WHY no site loads even though the button says connected.
 */
@Composable
fun DiagnosticsPanel(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val checks by DiagnosticsLog.checks.collectAsState()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val overall = overallState(checks)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header (tap to expand).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusDot(color = overall.color, size = 12.dp)
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(R.string.diag_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = androidx.compose.ui.res.stringResource(overall.captionRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f),
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    checks.forEach { CheckRow(it) }

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { scope.launch { Diagnostics.run() } },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(androidx.compose.ui.res.stringResource(R.string.diag_run))
                        }
                        TextButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(DiagnosticsLog.exportText()))
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.diag_copied),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            },
                        ) {
                            Text(androidx.compose.ui.res.stringResource(R.string.diag_copy))
                        }
                        TextButton(onClick = { DiagnosticsLog.clear() }) {
                            Text(androidx.compose.ui.res.stringResource(R.string.diag_clear))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    LogConsole()
                }
            }
        }
    }
}

private data class Overall(val color: Color, val captionRes: Int)

private fun overallState(checks: List<ComponentCheck>): Overall = when {
    checks.isEmpty() -> Overall(studio.cluvex.aether.ui.theme.OnDarkMuted, R.string.diag_idle)
    checks.any { it.state == CheckState.FAIL } -> Overall(studio.cluvex.aether.ui.theme.AetherError, R.string.diag_problem)
    checks.all { it.state == CheckState.PASS } -> Overall(studio.cluvex.aether.ui.theme.AetherMint, R.string.diag_all_ok)
    else -> Overall(studio.cluvex.aether.ui.theme.AetherCyan, R.string.diag_idle)
}

@Composable
private fun CheckRow(check: ComponentCheck) {
    val color = when (check.state) {
        CheckState.PASS -> studio.cluvex.aether.ui.theme.AetherMint
        CheckState.FAIL -> studio.cluvex.aether.ui.theme.AetherError
        CheckState.RUNNING -> studio.cluvex.aether.ui.theme.AetherCyan
        CheckState.PENDING -> studio.cluvex.aether.ui.theme.OnDarkMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot(color = color, size = 10.dp)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = check.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (check.detail.isNotBlank()) {
                Text(
                    text = check.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = check.state.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

@Composable
private fun LogConsole() {
    // 1.2.2 UI-SPEED FIX: the log list was collected by the panel itself, so
    // every engine line (hundreds during a scan) recomposed the whole
    // diagnostics card — and the whole drawer around it — even while the log
    // console was collapsed and invisible. The console is only composed when
    // it is open, and it is the only thing subscribed to the log now.
    val lines: List<LogLine> = DiagnosticsLog.lines.collectAsState().value
    val scroll = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 260.dp)
            .background(studio.cluvex.aether.ui.theme.Navy800, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .verticalScroll(scroll),
    ) {
        if (lines.isEmpty()) {
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.diag_empty_logs),
                style = MaterialTheme.typography.bodySmall,
                color = studio.cluvex.aether.ui.theme.OnDarkMuted,
            )
        } else {
            Column {
                lines.forEach { line ->
                    Text(
                        text = line.format(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = logColor(line.level),
                    )
                }
            }
        }
    }
}

private fun logColor(level: LogLevel): Color = when (level) {
    LogLevel.ERROR -> studio.cluvex.aether.ui.theme.AetherError
    LogLevel.WARN -> studio.cluvex.aether.ui.theme.AetherCyan
    LogLevel.INFO -> studio.cluvex.aether.ui.theme.OnDark
    LogLevel.DEBUG -> studio.cluvex.aether.ui.theme.OnDarkMuted
}

@Composable
private fun StatusDot(color: Color, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
    )
}
