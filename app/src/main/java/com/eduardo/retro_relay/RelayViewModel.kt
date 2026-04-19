package com.eduardo.retro_relay

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduardo.retro_relay.data.PrefsManager
import com.eduardo.retro_relay.network.ApiService
import com.eduardo.retro_relay.network.MdnsHelper
import com.eduardo.retro_relay.storage.ConsoleStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

data class Canal(
    val index: Int,
    val nome: String,
    val ativo: Boolean = false
) {
    fun isPlaceholder(): Boolean = index == -1
}

class RelayViewModel : ViewModel() {

    data class ConsoleConfig(
        val nome: String,
        val imagem: String // Pode ser URL do CDN ou URI local como string
    )

    private lateinit var appContext: Context

    fun initStorage(context: Context) {
        appContext = context.applicationContext
        viewModelScope.launch {
            val carregado = ConsoleStorage.carregar(appContext)
            _consoles.value = carregado
        }
    }


    // Lista dos canais fixos (0 a 15)
    val canais = mutableStateListOf<Canal>()

    // Modo único (apenas 1 canal pode estar ativo por vez)
    var modoUnico by mutableStateOf(true)
        private set

    // Estado de conectividade com o backend
    var conectado by mutableStateOf(false)
        private set

    // Armazena a configuração personalizada por canal
    private val _consoles = MutableStateFlow<Map<Int, ConsoleConfig>>(emptyMap())
    val consoles: StateFlow<Map<Int, ConsoleConfig>> = _consoles

    // Job para monitoramento contínuo
    private var monitorJob: Job? = null

    init {
        // Inicializa canais nomeados ou placeholders até 16
        val nomesConsoles = listOf(
            "Super Nintendo",
            "Mega Drive",
            "PlayStation",
            "Nintendo 64",
            "Sega Saturn",
            "Dreamcast",
            "GameCube",
            "Atari 2600"
        )

        nomesConsoles.forEachIndexed { i, nome ->
            canais.add(Canal(index = i, nome = nome))
        }

        while (canais.size < 16) {
            canais.add(Canal(index = canais.size, nome = ""))
        }
    }

    fun toggleCanal(index: Int) {
        val canal = canais.firstOrNull { it.index == index } ?: return
        val i = canais.indexOfFirst { it.index == index }

        if (modoUnico) {
            if (canal.ativo) {
                // Desativa todos se clicar no mesmo canal ativo
                canais.indices.forEach { j ->
                    canais[j] = canais[j].copy(ativo = false)
                }
            } else {
                // Ativa somente o canal clicado
                canais.indices.forEach { j ->
                    canais[j] = canais[j].copy(ativo = false)
                }
                canais[i] = canal.copy(ativo = true)
            }
        } else {
            // Alterna o estado normalmente
            canais[i] = canal.copy(ativo = !canal.ativo)
        }

        ApiService.toggleCanal(index)
    }


    fun setModo(unico: Boolean) {
        modoUnico = unico

        if (unico) {
            val primeiroAtivo = canais.indexOfFirst { it.ativo }
            canais.indices.forEach { i ->
                canais[i] = canais[i].copy(ativo = i == primeiroAtivo)
            }
        }

        ApiService.setModo(if (unico) "unico" else "multi")
    }

    fun iniciarMonitoramento() {
        if (monitorJob?.isActive == true) return

        monitorJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val ok = try {
                    ApiService.testarConexao()
                } catch (_: Exception) {
                    false
                }

                withContext(Dispatchers.Main) {
                    conectado = ok
                }

                delay(15_000)
            }
        }
    }

    var ipRetroRelay by mutableStateOf<String?>(null)
        private set

    private var mdnsHelper: MdnsHelper? = null

    var ipMdns by mutableStateOf<String?>(null)
        private set

    fun iniciarMdns(context: Context) {
        MdnsHelper(context) { ip ->
            ipMdns = ip
            PrefsManager.setIp(ip)
            ApiService.ipRetroRelay = ip
            Log.d("mDNS", "IP mDNS detectado: $ip")
        }.startDiscovery()
    }

    override fun onCleared() {
        monitorJob?.cancel()
        mdnsHelper?.stopDiscovery()
        super.onCleared()
    }

    fun salvarConsole(canal: Int, nome: String, imagem: String) {
        val novoMapa = _consoles.value.toMutableMap().apply {
            put(canal, ConsoleConfig(nome, imagem))
        }
        _consoles.value = novoMapa

        // Salva persistente
        viewModelScope.launch {
            ConsoleStorage.salvar(appContext, novoMapa)
        }
    }

    fun getConsolePorCanal(canal: Int): ConsoleConfig? = _consoles.value[canal]

    fun removerConsole(index: Int) {
        val novoMapa = _consoles.value.toMutableMap().apply {
            remove(index)
        }
        _consoles.value = novoMapa

        // Atualiza o armazenamento
        viewModelScope.launch {
            ConsoleStorage.salvar(appContext, novoMapa)
        }
    }

    fun salvarImagemLocal(uri: Uri): String {
        val inputStream = appContext.contentResolver.openInputStream(uri) ?: return ""
        val fileName = "console_${System.currentTimeMillis()}.jpg"
        val file = File(appContext.filesDir, fileName)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }


}
