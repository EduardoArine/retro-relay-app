package com.eduardo.retro_relay.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.eduardo.retro_relay.RelayViewModel.ConsoleConfig
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("console_config")

@Serializable
data class PersistedConsole(val canal: Int, val nome: String, val imagem: String)

object ConsoleStorage {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    private val CONSOLES_KEY = stringPreferencesKey("consoles_json")

    suspend fun salvar(context: Context, consoles: Map<Int, ConsoleConfig>) {
        val list = consoles.map { PersistedConsole(it.key, it.value.nome, it.value.imagem) }
        val jsonString = json.encodeToString(list)
        context.dataStore.edit { prefs ->
            prefs[CONSOLES_KEY] = jsonString
        }
    }

    suspend fun carregar(context: Context): Map<Int, ConsoleConfig> {
        val prefs = context.dataStore.data.first()
        val jsonString = prefs[CONSOLES_KEY] ?: return emptyMap()

        return try {
            val list = json.decodeFromString<List<PersistedConsole>>(jsonString)
            list.associate { it.canal to ConsoleConfig(it.nome, it.imagem) }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
