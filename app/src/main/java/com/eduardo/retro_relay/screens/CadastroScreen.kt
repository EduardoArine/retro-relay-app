package com.eduardo.retro_relay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.ui.components.HeaderBarWithBack
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(viewModel: RelayViewModel, onBackClick: () -> Unit, onCanalClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181111))
    ) {
        val consoles = viewModel.consoles.collectAsState().value
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                HeaderBarWithBack(
                    title = "Cadastrar Canais",
                    onBackClick = onBackClick
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFF181111)
        ) { padding ->
            LazyColumn(modifier = Modifier
                .padding(padding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()) {

                items(16) { index ->
                    val console = consoles[index]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCanalClick(index) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ícone/imagem
                        if (console == null) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF2A1B1B), shape = RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF5A4A4A), shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        } else {
                            AsyncImage(
                                model = console.imagem,
                                contentDescription = console.nome,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = console?.nome ?: "Canal ${index + 1}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (console != null) "Configurado" else "Não configurado",
                                color = Color(0xFFb89d9f),
                                fontSize = 14.sp
                            )
                        }

                        // Botão remover
                        if (console != null) {
                            TextButton(
                                onClick = {
                                    viewModel.removerConsole(index)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Canal ${index + 1} removido com sucesso")
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Remover")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsoleItem(nome: String, subtitulo: String, imagemUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* Futuro: abrir detalhes ou edição */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imagemUrl,
            contentDescription = nome,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(2.dp) // margem interna para suavizar recorte
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(nome, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitulo, color = Color(0xFFb89d9f), fontSize = 14.sp)
        }
    }
}