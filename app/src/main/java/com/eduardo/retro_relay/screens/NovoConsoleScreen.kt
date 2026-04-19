package com.eduardo.retro_relay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduardo.retro_relay.RelayViewModel
import coil.compose.AsyncImage
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.eduardo.retro_relay.ui.components.HeaderBarWithBack
import kotlinx.coroutines.launch

@Composable
fun NovoConsoleScreen(
    viewModel: RelayViewModel,
    canal: Int,
    onBackClick: () -> Unit,
) {
    val configExistente = viewModel.getConsolePorCanal(canal)

    var nome by rememberSaveable { mutableStateOf(configExistente?.nome ?: "") }
    var imagemSelecionada by rememberSaveable { mutableStateOf(configExistente?.imagem ?: "") }

    val uriSelecionada = rememberSaveable { mutableStateOf<Uri?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriSelecionada.value = it
            imagemSelecionada = it.toString()
        }
    }

    val imagensCdn = listOf(
        // 🎮 Atari
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/atari_2600.png",

        // 🍄 Nintendo
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/nes.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/snes.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/n64.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/game_cube.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/nintendo_wii.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/nintendo_switch.png",

        // 🌀 Sega
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/master_system.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/mega_drive.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/sega_saturn.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/sega_dreamcast.png",

        // 🎮 Sony PlayStation
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/ps1.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/ps2.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/ps3.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/ps4.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/ps5.png",

        // 🟩 Microsoft Xbox
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/xbox.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/xbox_360.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/xbox_one.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/xbox_series_s.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/xbox_series_x.png",

        // 🧨 SNK Neo Geo
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/neogeo.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/neogeo_cd.png",

        // 🧠 PC Engine / TurboGrafx
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/pc_engine.png",
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/turbo_grafx.png",

        // 📀 3DO
        "https://raw.githubusercontent.com/EduardoArine/retro-relay-images/refs/heads/main/3do.png"
    )

    Scaffold(
        containerColor = Color(0xFF181111),
        topBar = {
            HeaderBarWithBack(
                title = if (configExistente != null) "Editar Console ${canal + 1}" else "Cadastrar no Canal ${canal + 1}",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Console", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Escolha uma imagem", color = Color.White, fontSize = 16.sp)
                TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text(
                        text = if (uriSelecionada.value != null) "Imagem carregada" else "Selecionar do dispositivo",
                        color = if (uriSelecionada.value != null) Color(0xFF00C853) else Color(0xFFE92932)
                    )
                }
            }

            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                imagensCdn.forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp)
                            .background(if (url == imagemSelecionada) Color.White else Color.Transparent)
                            .clickable { imagemSelecionada = url }
                    )
                }

                uriSelecionada.value?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagem Local",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp)
                            .background(
                                if (imagemSelecionada == uri.toString()) Color.White else Color.Transparent
                            )
                            .clickable {
                                imagemSelecionada = uri.toString()
                            }
                    )
                }
            }

            if (imagemSelecionada.isNotBlank()) {
                Text("Pré-visualização da imagem", color = Color.White, fontSize = 16.sp)
                AsyncImage(
                    model = imagemSelecionada,
                    contentDescription = "Imagem Selecionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Black, shape = RoundedCornerShape(12.dp))
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Limpar
            TextButton(onClick = {
                nome = ""
                imagemSelecionada = ""
                uriSelecionada.value = null
            }) {
                Text("Limpar campos", color = Color.LightGray)
            }

            Button(
                onClick = {
                    if (nome.isBlank() || imagemSelecionada.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Preencha todos os campos antes de salvar.")
                        }
                    } else {
                        val caminhoFinal = if (uriSelecionada.value != null) {
                            viewModel.salvarImagemLocal(uriSelecionada.value!!)
                        } else {
                            imagemSelecionada // já é uma URL do CDN
                        }

                        viewModel.salvarConsole(canal, nome, caminhoFinal)

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Console salvo com sucesso!")
                        }
                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE92932))
            ) {
                Text("Salvar", color = Color.White)
            }
        }
    }
}



