package studio.cluvex.aether.ui

import android.os.SystemClock
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import studio.cluvex.aether.R
import studio.cluvex.aether.core.IpEndpoint
import studio.cluvex.aether.model.ConnectionProfile
import studio.cluvex.aether.model.ConnectionState
import studio.cluvex.aether.model.PsiphonRegion
import studio.cluvex.aether.model.isBusy
import studio.cluvex.aether.model.isConnected
import studio.cluvex.aether.ui.components.ButtonMode
import studio.cluvex.aether.ui.components.ConnectButton
import studio.cluvex.aether.ui.components.DiagnosticsPanel
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherError
import studio.cluvex.aether.ui.theme.AetherMint

// ═══════════════════════════════════════════════════════════════
//  ROOT — tab-based navigation, NO drawer, NO bottom sheets
// ═══════════════════════════════════════════════════════════════

@Composable
fun HomeScreen(
    state: ConnectionState,
    profile: ConnectionProfile,
    connectedSince: Long?,
    ipInfo: IpEndpoint?,
    ipLoading: Boolean,
    onProfileChange: (ConnectionProfile) -> Unit,
    onToggleConnection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val settingsEnabled = state is ConnectionState.Idle || state is ConnectionState.Error

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            PsitherBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> ConnectPage(
                    state = state,
                    profile = profile,
                    connectedSince = connectedSince,
                    ipInfo = ipInfo,
                    ipLoading = ipLoading,
                    onToggleConnection = onToggleConnection,
                    onOpenServers = { selectedTab = 1 },
                )
                1 -> ServersPage(
                    selectedRegion = profile.psiphonRegion,
                    onSelectRegion = { region ->
                        onProfileChange(
                            profile.copy(
                                psiphonRegion = region,
                                psiphonEnabled = region != PsiphonRegion.DIRECT,
                            ),
                        )
                        selectedTab = 0
                    },
                    enabled = settingsEnabled,
                )
                2 -> SettingsPage(
                    state = state,
                    profile = profile,
                    onProfileChange = onProfileChange,
                    enabled = settingsEnabled,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  BOTTOM NAV — custom dark bar with gold accent
// ═══════════════════════════════════════════════════════════════

@Composable
private fun PsitherBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF050505))
            .navigationBarsPadding(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(AetherBlue.copy(alpha = 0.25f))
                .align(Alignment.TopCenter),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PsitherTab(Icons.Rounded.Bolt, "Connect", selectedTab == 0) { onTabSelected(0) }
            PsitherTab(Icons.Rounded.Public, "Servers", selectedTab == 1) { onTabSelected(1) }
            PsitherTab(Icons.Rounded.Tune, "Settings", selectedTab == 2) { onTabSelected(2) }
        }
    }
}

@Composable
private fun PsitherTab(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) AetherBlue else Color.Gray,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) AetherBlue else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  TAB 0 — CONNECT PAGE
// ═══════════════════════════════════════════════════════════════

@Composable
private fun ConnectPage(
    state: ConnectionState,
    profile: ConnectionProfile,
    connectedSince: Long?,
    ipInfo: IpEndpoint?,
    ipLoading: Boolean,
    onToggleConnection: () -> Unit,
    onOpenServers: () -> Unit,
) {
    val mode = when {
        state.isConnected -> ButtonMode.CONNECTED
        state.isBusy -> ButtonMode.BUSY
        state is ConnectionState.Error -> ButtonMode.ERROR
        else -> ButtonMode.IDLE
    }
    val accentColor = when (mode) {
        ButtonMode.CONNECTED -> AetherMint
        ButtonMode.ERROR -> AetherError
        else -> AetherBlue
    }
    val animatedAccent by animateColorAsState(accentColor, tween(600), label = "accent")

    // Live timer
    var elapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(connectedSince) {
        if (connectedSince != null) {
            while (true) {
                elapsed = (System.currentTimeMillis() - connectedSince) / 1000
                delay(1000)
            }
        } else {
            elapsed = 0
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Background glow
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color.Black)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(animatedAccent.copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.35f),
                    radius = size.minDimension * 0.9f,
                ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(28.dp))

            // Logo
            Text("P", fontSize = 38.sp, fontWeight = FontWeight.Black, color = AetherBlue)
            Text(
                "PSITHER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                color = AetherBlue.copy(alpha = 0.6f),
            )

            Spacer(Modifier.height(24.dp))

            // Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                val dotTr = rememberInfiniteTransition(label = "dot")
                val dotAlpha by dotTr.animateFloat(
                    0.3f, 1f,
                    infiniteRepeatable(tween(900), RepeatMode.Reverse),
                    label = "da",
                )
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(animatedAccent.copy(alpha = if (state.isBusy) dotAlpha else 1f)),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    stateTitle(state).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = animatedAccent,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(stateSubtitle(state), fontSize = 12.sp, color = Color.Gray)

            // Button
            Spacer(Modifier.weight(1f))
            ConnectButton(mode = mode, onClick = onToggleConnection)
            Spacer(Modifier.weight(0.5f))

            // Stats strip
            if (state.isConnected) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0A0A0A))
                        .border(1.dp, AetherMint.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatCell("DURATION", "%02d:%02d:%02d".format(elapsed / 3600, (elapsed % 3600) / 60, elapsed % 60), Color.White)
                    Box(Modifier.width(1.dp).height(36.dp).background(Color.DarkGray))
                    StatCell("EXIT IP", if (ipLoading) "..." else ipInfo?.ip ?: "--", Color.White)
                    Box(Modifier.width(1.dp).height(36.dp).background(Color.DarkGray))
                    StatCell("COUNTRY", ipInfo?.countryCode ?: "--", AetherMint)
                }
                Spacer(Modifier.height(14.dp))
            }

            // Server card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A0A0A))
                    .border(1.dp, AetherBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .clickable { onOpenServers() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(profile.psiphonRegion.flag, fontSize = 28.sp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            profile.psiphonRegion.enName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Text(
                            if (profile.psiphonEnabled) "Psiphon Chained" else "Direct WARP",
                            fontSize = 12.sp,
                            color = if (profile.psiphonEnabled) AetherMint else Color.Gray,
                        )
                    }
                    Text("\u25B8", fontSize = 20.sp, color = AetherBlue)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ═══════════════════════════════════════════════════════════════
//  TAB 1 — SERVERS PAGE (full-screen country grid)
// ═══════════════════════════════════════════════════════════════

@Composable
private fun ServersPage(
    selectedRegion: PsiphonRegion,
    onSelectRegion: (PsiphonRegion) -> Unit,
    enabled: Boolean,
) {
    var query by remember { mutableStateOf("") }
    val all = remember { PsiphonRegion.entries.toList() }
    val filtered = remember(query) {
        if (query.isBlank()) all
        else all.filter {
            it.enName.contains(query, ignoreCase = true) ||
                it.faName.contains(query, ignoreCase = true) ||
                it.code.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Text("LOCATIONS", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp, color = AetherBlue)
        Spacer(Modifier.height(4.dp))
        Text("Select your exit country", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(filtered) { region ->
                CountryCard(region, region == selectedRegion) {
                    if (enabled) onSelectRegion(region)
                }
            }
        }
    }
}

@Composable
private fun CountryCard(
    region: PsiphonRegion,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) AetherBlue else Color(0xFF1A1A1A),
                RoundedCornerShape(16.dp),
            )
            .background(if (selected) AetherBlue.copy(alpha = 0.08f) else Color(0xFF0A0A0A))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(region.flag, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                region.enName,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) AetherBlue else Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  TAB 2 — SETTINGS PAGE (full-screen, all panels)
// ═══════════════════════════════════════════════════════════════

@Composable
private fun SettingsPage(
    state: ConnectionState,
    profile: ConnectionProfile,
    onProfileChange: (ConnectionProfile) -> Unit,
    enabled: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Text("SETTINGS", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp, color = AetherBlue)
        Spacer(Modifier.height(4.dp))
        Text("Configure your connection", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(20.dp))

        SectionHeader("ADVANCED")
        AdvancedPanel(profile = profile, onProfileChange = onProfileChange, enabled = enabled, startExpanded = true)

        Spacer(Modifier.height(16.dp))
        SectionHeader("DIAGNOSTICS")
        DiagnosticsPanel()

        Spacer(Modifier.height(16.dp))
        SectionHeader("SHARING")
        SharePanel(state = state, profile = profile, onProfileChange = onProfileChange)

        Spacer(Modifier.height(16.dp))
        SectionHeader("ABOUT")
        AboutPanel()

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(AetherBlue.copy(alpha = 0.3f)))
        Text(
            title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            color = AetherBlue.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Box(Modifier.weight(1f).height(1.dp).background(AetherBlue.copy(alpha = 0.3f)))
    }
}

// ═══════════════════════════════════════════════════════════════
//  HELPERS
// ═══════════════════════════════════════════════════════════════

@Composable
private fun stateTitle(state: ConnectionState): String = when (state) {
    is ConnectionState.Idle -> stringResource(R.string.state_idle)
    is ConnectionState.Launching -> stringResource(R.string.state_launching)
    is ConnectionState.Connecting -> stringResource(R.string.state_connecting)
    is ConnectionState.Verifying -> stringResource(R.string.state_verifying)
    is ConnectionState.Connected -> stringResource(R.string.state_connected)
    is ConnectionState.Reconnecting -> stringResource(R.string.state_reconnecting)
    is ConnectionState.Disconnecting -> stringResource(R.string.state_disconnecting)
    is ConnectionState.Error -> stringResource(R.string.state_error)
}

@Composable
private fun stateSubtitle(state: ConnectionState): String = when (state) {
    is ConnectionState.Idle -> stringResource(R.string.tap_to_connect)
    is ConnectionState.Connected -> stringResource(R.string.tap_to_disconnect)
    is ConnectionState.Reconnecting ->
        stringResource(R.string.reconnect_attempt, state.attempt, state.maxAttempts)
    is ConnectionState.Error -> state.message
    else -> stringResource(R.string.tap_to_disconnect)
}
