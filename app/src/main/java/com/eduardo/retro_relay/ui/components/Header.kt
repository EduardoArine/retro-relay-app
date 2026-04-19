package com.eduardo.retro_relay.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduardo.retro_relay.navigation.Routes

@Composable
fun HeaderBar(
    title: String,
    ouvindo: Boolean = false, // controla o estado de captura de voz
    onMicClick: (() -> Unit)? = null
) {
    val icon = if (ouvindo) Icons.Default.Mic else Icons.Default.MicNone
    val micColor by animateColorAsState(
        targetValue = if (ouvindo) Color.Red else Color.White,
        label = "MicColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (onMicClick != null) {
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Comando por Voz",
                    tint = micColor
                )
            }
        }
    }
}

