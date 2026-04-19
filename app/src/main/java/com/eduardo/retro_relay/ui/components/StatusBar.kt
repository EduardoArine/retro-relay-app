package com.eduardo.retro_relay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.network.ApiService

// ✅ Status de conexão + modo único/vários
@Composable
fun StatusBar(viewModel: RelayViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val statusColor = if (viewModel.conectado) Color(0xFF4CAF50) else Color(0xFFF44336)
        val statusText = if (viewModel.conectado) "Conectado" else "Desconectado"

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(statusColor, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$statusText (${ApiService.ipRetroRelay})",
            fontSize = 13.sp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.weight(1f))

        Text("Único", fontSize = 13.sp, color = Color.White)
        Switch(
            checked = !viewModel.modoUnico,
            onCheckedChange = { viewModel.setModo(!it) },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
        )
        Text("Vários", fontSize = 13.sp, color = Color.White)
    }
}