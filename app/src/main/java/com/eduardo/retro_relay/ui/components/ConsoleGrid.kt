package com.eduardo.retro_relay.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduardo.retro_relay.Canal
import androidx.compose.material3.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eduardo.retro_relay.RelayViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.eduardo.retro_relay.navigation.Routes


// ✅ Grid com consoles
@Composable
fun ConsoleGrid(
    canais: List<Canal>,
    consoles: Map<Int, RelayViewModel.ConsoleConfig>,
    navController: NavHostController,
    onToggle: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cardHeight = this@BoxWithConstraints.maxHeight / 8

        val canaisConfigurados = canais.filter { !consoles[it.index]?.nome.isNullOrBlank() }

        if (canaisConfigurados.isEmpty()) {
            // ✅ Mensagem e botão centralizados
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nenhum console configurado ainda.",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate(Routes.CADASTRO) // Exemplo: abre cadastro para canal 0
                }) {
                    Text(text = "Ir para Cadastro")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(canaisConfigurados) { canal ->
                    ConsoleCard(
                        canal = canal,
                        config = consoles[canal.index],
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                    ) {
                        onToggle(canal.index)
                    }
                }
            }
        }


//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(12.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // caso queira apresentar todos canais na home
//            // items(canais) { canal ->
//            items(canais.filter { !consoles[it.index]?.nome.isNullOrBlank() }) { canal ->
//                ConsoleCard(
//                    canal = canal,
//                    config = consoles[canal.index],
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(cardHeight)
//                ) {
//                    if (consoles[canal.index]?.nome.isNullOrBlank()) {
//                        navController.navigate(Routes.novoConsoleRoute(canal.index))
//                    } else {
//                        onToggle(canal.index)
//                    }
//                }
//            }
//        }
    }
}

@Composable
fun ConsoleCard(
    canal: Canal,
    config: RelayViewModel.ConsoleConfig?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (canal.isPlaceholder()) {
        Box(
            modifier = modifier
                .background(Color(0xFF1A1A1A), shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF2A2A2A), shape = RoundedCornerShape(12.dp))
        )
        return
    }

    val targetScale = if (canal.ativo) 1.05f else 1f
    val scale by animateFloatAsState(targetValue = targetScale, animationSpec = tween(300))

    val isEmpty = config == null || config.nome.isBlank()
    val backgroundColor = if (canal.ativo) Color(0xFF12470E) else Color(0xFF251E1E)
    val borderColor = if (canal.ativo) Color(0xFF23FF15) else Color(0xFF4F4040)

    Row(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .background(if (isEmpty) Color(0xFF121212) else backgroundColor, shape = RoundedCornerShape(12.dp))
            .border(2.dp, if (isEmpty) Color(0xFF444444) else borderColor, shape = RoundedCornerShape(12.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isEmpty) {
            // ✅ Imagem e nome do console
            AsyncImage(
                model = config!!.imagem,
                contentDescription = config.nome,
                contentScale = ContentScale.Crop, // Isso centraliza e corta
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(1f) // Garante proporção quadrada
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray) // Cor de fundo padrão
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = config?.nome?.ifBlank { "Canal ${canal.index + 1}" } ?: "Canal ${canal.index + 1}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        } else {
            // ✅ Ícone "+" com "Canal X" abaixo e fundo diferenciado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Console",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = "Canal ${canal.index + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

        }
    }
}
