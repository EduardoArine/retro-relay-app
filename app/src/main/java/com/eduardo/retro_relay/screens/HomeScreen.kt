package com.eduardo.retro_relay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.navigation.Routes
import com.eduardo.retro_relay.ui.components.ConsoleGrid
import com.eduardo.retro_relay.ui.components.HeaderBar

@Composable
fun HomeScreen(
    viewModel: RelayViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val consoles by viewModel.consoles.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.iniciarMonitoramento()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF181111))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        var ouvindo by remember { mutableStateOf(false) }
        
        HeaderBar(
            title = "RetroRelay",
            ouvindo = ouvindo,
            onMicClick = {
                ouvindo = !ouvindo
                navController.navigate(Routes.VOICE)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ConsoleGrid(
            canais = viewModel.canais,
            consoles = consoles,
            navController = navController,
            onToggle = { viewModel.toggleCanal(it) }
        )
    }
}
