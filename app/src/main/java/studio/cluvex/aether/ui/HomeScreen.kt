package studio.cluvex.aether.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.cluvex.aether.R
import studio.cluvex.aether.model.ConnectionProfile
import studio.cluvex.aether.model.ConnectionState
import studio.cluvex.aether.ui.components.ConnectButton
import studio.cluvex.aether.ui.components.ButtonMode
import studio.cluvex.aether.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: ConnectionState,
    profile: ConnectionProfile,
    connectedSince: Long?,
    ipInfo: studio.cluvex.aether.model.IpInfo?,
    ipLoading: Boolean,
    settingsEnabled: Boolean,
    onToggleConnection: () -> Unit,
    onProfileChange: (ConnectionProfile) -> Unit,
    onCheckIp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showLocations by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF070707),
                contentColor = AetherBlue
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AetherBlue,
                        unselectedIconColor = Color.DarkGray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Public, contentDescription = "Locations") },
                    selected = currentTab == 1,
                    onClick = { showLocations = true },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AetherBlue,
                        unselectedIconColor = Color.DarkGray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    selected = currentTab == 2,
                    onClick = { showSettings = true },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AetherBlue,
                        unselectedIconColor = Color.DarkGray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 48.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header (Logo + PSITHER)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "P",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = AetherBlue
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "PSITHER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = AetherBlue
                )
            }

            Spacer(Modifier.height(32.dp))

            // Subtitle
            val statusColor = if (state.isConnected) AetherMint else AetherError
            val statusText = when (state) {
                is ConnectionState.Connected -> "SECURE"
                is ConnectionState.Idle -> "DISCONNECTED"
                is ConnectionState.Error -> "CONNECTION FAILED"
                else -> "CONNECTING..."
            }
            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Secure your network with elite encryption.",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.weight(1f))

            // Massive Central Connect Button
            val btnMode = when (state) {
                is ConnectionState.Idle -> ButtonMode.IDLE
                is ConnectionState.Connected -> ButtonMode.CONNECTED
                is ConnectionState.Error -> ButtonMode.ERROR
                else -> ButtonMode.BUSY
            }
            ConnectButton(mode = btnMode, onClick = onToggleConnection)

            Spacer(Modifier.weight(1f))

            // Server Selection Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0F0F))
                    .clickable(enabled = !state.isConnected) { showLocations = true }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(profile.psiphonRegion.flag, color = Color.White, fontSize = 16.sp)
                    }
                    
                    Spacer(Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.psiphonRegion.enName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Row {
                            Text(if (state.isConnected) "Status: " else "Status: ", color = Color.Gray, fontSize = 12.sp)
                            Text(if (state.isConnected) "Encrypted" else "Standby", color = if(state.isConnected) AetherMint else Color.Gray, fontSize = 12.sp)
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Rounded.FlashOn,
                        contentDescription = null,
                        tint = AetherBlue
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
        }

        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                containerColor = Navy800
            ) {
                AdvancedPanel(
                    profile = profile,
                    onProfileChange = onProfileChange,
                    enabled = !state.isConnected
                )
            }
        }

        if (showLocations) {
            studio.cluvex.aether.ui.components.CountrySelectorSheet(
                selectedRegion = profile.psiphonRegion,
                onSelectRegion = { newRegion ->
                    onProfileChange(profile.copy(psiphonRegion = newRegion))
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showLocations = false
                        }
                    }
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showLocations = false
                        }
                    }
                },
                sheetState = sheetState
            )
        }
    }
}
