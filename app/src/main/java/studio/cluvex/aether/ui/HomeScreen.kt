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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import studio.cluvex.aether.R
import studio.cluvex.aether.core.IpEndpoint
import studio.cluvex.aether.model.ConnectionProfile
import studio.cluvex.aether.model.ConnectionState
import studio.cluvex.aether.model.PsiphonRegion
import studio.cluvex.aether.model.isBusy
import studio.cluvex.aether.model.isConnected
import studio.cluvex.aether.ui.components.ButtonMode
import studio.cluvex.aether.ui.components.ConnectButton
import studio.cluvex.aether.ui.components.CountrySelectorSheet
import studio.cluvex.aether.ui.components.DiagnosticsPanel
import studio.cluvex.aether.ui.theme.AetherBlue
import studio.cluvex.aether.ui.theme.AetherError
import studio.cluvex.aether.ui.theme.AetherMint
import studio.cluvex.aether.ui.theme.Navy800

@OptIn(ExperimentalMaterial3Api::class)
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
    // ── Derived state ──
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

    val settingsEnabled = state is ConnectionState.Idle || state is ConnectionState.Error

    // ── Drawer (Diagnostics / Share / Advanced / About) ──
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    val drawerVisible = drawerState.isOpen || drawerState.targetValue == DrawerValue.Open

    // ── Settings bottom sheet (quick access from top bar) ──
    var showSettingsSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Country selector ──
    var showCountrySheet by remember { mutableStateOf(false) }
    val countrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Connection timer ──
    var elapsedSeconds by remember { mutableStateOf(0L) }
    LaunchedEffect(connectedSince) {
        if (connectedSince != null) {
            while (true) {
                elapsedSeconds = (SystemClock.elapsedRealtime() - connectedSince) / 1000
                delay(1000)
            }
        } else {
            elapsedSeconds = 0
        }
    }

    // ════════════════════════════════════════════════════════
    // DRAWER  (side panel with all feature panels)
    // ════════════════════════════════════════════════════════
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Navy800,
                modifier = Modifier.fillMaxWidth(0.88f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = "PSITHER",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = AetherBlue,
                    )
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(20.dp))

                    if (drawerVisible) {
                        DiagnosticsPanel()

                        Spacer(Modifier.height(16.dp))

                        SharePanel(
                            state = state,
                            profile = profile,
                            onProfileChange = onProfileChange,
                        )

                        Spacer(Modifier.height(16.dp))

                        AdvancedPanel(
                            profile = profile,
                            onProfileChange = onProfileChange,
                            enabled = settingsEnabled,
                        )

                        Spacer(Modifier.height(16.dp))

                        AboutPanel()
                    }
                }
            }
        },
    ) {
        // ════════════════════════════════════════════════════════
        // MAIN SCREEN
        // ════════════════════════════════════════════════════════
        Box(modifier = modifier.fillMaxSize()) {
            // Custom background: pure black + subtle radial accent glow
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Color.Black)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedAccent.copy(alpha = 0.06f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width / 2f, size.height * 0.38f),
                        radius = size.minDimension * 0.85f,
                    ),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ── Top bar ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        drawerScope.launch { drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Menu,
                            contentDescription = stringResource(R.string.menu_open),
                            tint = Color.White,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "PSITHER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        color = AetherBlue,
                    )

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.advanced_open),
                            tint = Color.White,
                        )
                    }
                }

                // ── Status indicator ──
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotTransition = rememberInfiniteTransition(label = "dot")
                    val dotPulse by dotTransition.animateFloat(
                        initialValue = 0.35f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            tween(900),
                            RepeatMode.Reverse,
                        ),
                        label = "dotPulse",
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                animatedAccent.copy(
                                    alpha = if (state.isBusy) dotPulse else 1f,
                                ),
                            ),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stateTitle(state).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = animatedAccent,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stateSubtitle(state),
                    fontSize = 12.sp,
                    color = Color.Gray,
                )

                // ── Connect button (centre of screen) ──
                Spacer(Modifier.weight(1f))

                ConnectButton(mode = mode, onClick = onToggleConnection)

                Spacer(Modifier.weight(0.5f))

                // ── Connection info strip (only visible when connected) ──
                if (state.isConnected) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(
                                1.dp,
                                AetherMint.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp),
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        // Duration
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "DURATION",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                letterSpacing = 1.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            val h = elapsedSeconds / 3600
                            val m = (elapsedSeconds % 3600) / 60
                            val s = elapsedSeconds % 60
                            Text(
                                text = "%02d:%02d:%02d".format(h, m, s),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }

                        // Separator
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(Color.DarkGray),
                        )

                        // IP
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "EXIT IP",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                letterSpacing = 1.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            if (ipLoading) {
                                Text("...", fontSize = 14.sp, color = Color.Gray)
                            } else if (ipInfo != null) {
                                Text(
                                    text = ipInfo.ip,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            } else {
                                Text("--", fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        // Separator
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(Color.DarkGray),
                        )

                        // Country
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "COUNTRY",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                letterSpacing = 1.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = ipInfo?.countryCode ?: "--",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AetherMint,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                }

                // ── Server selector card ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0A0A0A))
                        .border(
                            1.dp,
                            AetherBlue.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp),
                        )
                        .clickable(enabled = settingsEnabled) {
                            showCountrySheet = true
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = profile.psiphonRegion.flag,
                            fontSize = 28.sp,
                        )

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.psiphonRegion.enName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                            Text(
                                text = if (profile.psiphonEnabled) "Psiphon Chained"
                                       else "Direct WARP",
                                fontSize = 12.sp,
                                color = if (profile.psiphonEnabled) AetherMint
                                        else Color.Gray,
                            )
                        }

                        Text(
                            text = "\u25B8",
                            fontSize = 20.sp,
                            color = AetherBlue,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // SETTINGS BOTTOM SHEET (quick-access from top-right icon)
    // ════════════════════════════════════════════════════════
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = settingsSheetState,
            containerColor = Navy800,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.92f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
            ) {
                var sheetReady by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { sheetReady = true }
                if (sheetReady) {
                    AdvancedPanel(
                        profile = profile,
                        onProfileChange = onProfileChange,
                        enabled = settingsEnabled,
                        startExpanded = true,
                    )
                } else {
                    Spacer(Modifier.height(320.dp))
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // COUNTRY SELECTOR SHEET
    // ════════════════════════════════════════════════════════
    if (showCountrySheet) {
        CountrySelectorSheet(
            selectedRegion = profile.psiphonRegion,
            onSelectRegion = { newRegion ->
                // ★ CRITICAL FIX: auto-enable Psiphon when a non-DIRECT region
                // is selected, so traffic actually chains through Psiphon.
                onProfileChange(
                    profile.copy(
                        psiphonRegion = newRegion,
                        psiphonEnabled = newRegion != PsiphonRegion.DIRECT,
                    ),
                )
            },
            onDismiss = { showCountrySheet = false },
            sheetState = countrySheetState,
        )
    }
}

// ── Helper composables ──

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
