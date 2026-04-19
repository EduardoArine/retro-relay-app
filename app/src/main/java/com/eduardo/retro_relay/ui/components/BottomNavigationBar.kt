package com.eduardo.retro_relay.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VideogameAsset
import androidx.compose.material.icons.rounded.VideogameAssetOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eduardo.retro_relay.navigation.Routes


@Composable
fun BottomNavigationBar(
    conectado: Boolean,
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onCadastroClick: () -> Unit,
    onConfigClick: () -> Unit
) {
    Surface(
        color = Color(0xFF251E1E),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 12.dp)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()), // ✅ CORRETO
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(
                    Icons.Rounded.Gamepad,
                    contentDescription = "Home",
                    tint = if (currentRoute == Routes.HOME) Color(0xFF2196F3) else Color.White
                )
            }
            IconButton(onClick = onCadastroClick) {
                Icon(
                    Icons.Rounded.CreateNewFolder,
                    contentDescription = "Cadastro",
                    tint = if (currentRoute == Routes.CADASTRO) Color(0xFF2196F3) else Color.White
                )
            }
            IconButton(onClick = onConfigClick) {
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = "Configuração",
                    tint = if (currentRoute == Routes.CONFIG) Color(0xFF2196F3) else Color.White
                )
            }

            val statusIcon = if (conectado) Icons.Rounded.VideogameAsset else Icons.Rounded.VideogameAssetOff
            val statusColor = if (conectado) Color(0xFF4CAF50) else Color(0xFFF44336)

            Icon(
                imageVector = statusIcon,
                contentDescription = "Status",
                tint = statusColor
            )
        }
    }
}
