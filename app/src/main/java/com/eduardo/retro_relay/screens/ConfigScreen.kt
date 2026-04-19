// screens/ConfigScreen.kt
package com.eduardo.retro_relay.screens

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.data.PrefsManager
import com.eduardo.retro_relay.network.ApiService
import com.eduardo.retro_relay.ui.components.BottomNavigationBar
import com.eduardo.retro_relay.ui.components.HeaderBarWithBack
import kotlinx.coroutines.*

@Composable
fun ConfigScreen(
    viewModel: RelayViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCadastroClick: () -> Unit,
    onConfigClick: () -> Unit,
) {

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    var ip by remember { mutableStateOf(PrefsManager.getIp()) }

    var emBusca by remember { mutableStateOf(true) }

    LaunchedEffect(viewModel.ipMdns) {
        viewModel.ipMdns?.let {
            ip = it
        }
    }

    var user by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var modoUnico by remember { mutableStateOf(viewModel.modoUnico) }

    LaunchedEffect(Unit) {
        viewModel.iniciarMdns(context)
        delay(2500) // tempo para buscar o ESP
        emBusca = false
    }

    Scaffold(
        containerColor = Color(0xFF171212),
        topBar = {
            HeaderBarWithBack(
                title = "Configurações",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF181111))
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Endereço IP", color = Color.White)

            if (emBusca) {
                Text("🔍 Procurando o ESP na rede...", color = Color.LightGray, fontSize = 13.sp)
            } else if (viewModel.ipMdns != null) {
                Text("✅ IP detectado automaticamente: ${viewModel.ipMdns}", color = Color(0xFF00C853), fontSize = 13.sp)
            } else {
                Text("❌ ESP não encontrado via mDNS", color = Color(0xFFE92932), fontSize = 13.sp)
            }

            OutlinedTextField(
                value = ip,
                onValueChange = { ip = it },
                placeholder = { Text("192.168.0.100", color = Color(0xFFb89d9f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF382929),
                    unfocusedContainerColor = Color(0xFF382929),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val ok = ApiService.testarConexao()
                        withContext(Dispatchers.Main) {
                            val msg = if (ok) "✅ Conectado com sucesso!" else "❌ Falha na conexão."
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF382929))
            ) {
                Text("Testar Conexão", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Usuário", color = Color.White)
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                placeholder = { Text("admin", color = Color(0xFFb89d9f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF382929),
                    unfocusedContainerColor = Color(0xFF382929),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )

            Text("Senha", color = Color.White)
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = { Text("senha", color = Color(0xFFb89d9f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF382929),
                    unfocusedContainerColor = Color(0xFF382929),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo de Operação", color = Color.White)
                Switch(
                    checked = !modoUnico,
                    onCheckedChange = { modoUnico = !it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFe82630),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF382929)
                    )
                )
            }

            Text(
                "Ativo = Multiple, vários consoles simultâneos." +
                "\nDesativado = Single, apenas um console",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    PrefsManager.setIp(ip)
                    PrefsManager.setModoUnico(modoUnico)
                    ApiService.ipRetroRelay = ip.trim()
                    viewModel.setModo(modoUnico)

                    CoroutineScope(Dispatchers.Main).launch {
                        snackbarHostState.showSnackbar("Configurações salvas!")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFe82630))
            ) {
                Text("Salvar", color = Color.White)
            }
        }
    }
}


