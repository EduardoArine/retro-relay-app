package com.eduardo.retro_relay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.eduardo.retro_relay.data.PrefsManager
import com.eduardo.retro_relay.navigation.MainNavigation
import com.eduardo.retro_relay.network.ApiService
import com.eduardo.retro_relay.ui.theme.AppThemeWrapper
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que o Compose desenhe atrás da status bar e navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Inicializa preferências e IP
        PrefsManager.init(this)
        ApiService.ipRetroRelay = PrefsManager.getIp()

        setContent {
            val navController = rememberNavController()
            val viewModel: RelayViewModel = viewModel()
            val context = LocalContext.current // ✅ Aqui obtemos o contexto corretamente

            viewModel.setModo(PrefsManager.getModoUnico())

            LaunchedEffect(Unit) {
                viewModel.initStorage(context)
                viewModel.iniciarMdns(context)
            }

            // Aplica as cores das barras com o wrapper
            AppThemeWrapper {
                MainNavigation(viewModel, navController)
            }
        }
    }
}