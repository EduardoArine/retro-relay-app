package com.eduardo.retro_relay.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ApiService {

    private val client = HttpClient(OkHttp)

    // IP do ESP32 - vamos tornar isso configurável depois
    var ipRetroRelay: String = "192.168.0.156"

    fun toggleCanal(canal: Int) {
        val url = "http://$ipRetroRelay/ligar?canal=$canal"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.get(url)
            } catch (e: Exception) {
                println("Erro ao enviar comando: $e")
            }
        }
    }

    fun setModo(valor: String) {
        val url = "http://$ipRetroRelay/modo?valor=$valor"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.get(url)
            } catch (e: Exception) {
                println("Erro ao definir modo: $e")
            }
        }
    }

    suspend fun testarConexao(): Boolean {
        return try {
            val response = client.get("http://$ipRetroRelay/ping")
            response.status.value == 200 && response.bodyAsText().contains("pong", ignoreCase = true)
        } catch (e: Exception) {
            e.printStackTrace() // Mostra erro no Logcat
            false
        }
    }


}
