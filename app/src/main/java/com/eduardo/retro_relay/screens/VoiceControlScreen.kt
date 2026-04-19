package com.eduardo.retro_relay.screens

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.eduardo.retro_relay.RelayViewModel
import com.eduardo.retro_relay.ui.components.HeaderBarWithBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun VoiceControlScreen(
    viewModel: RelayViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var comandoReconhecido by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                }

                speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { comando ->
                            comandoReconhecido = comando
                            interpretarComando(comando.lowercase(Locale.getDefault()), viewModel)
                        }
                    }

                    override fun onError(error: Int) {
                        comandoReconhecido = "Erro: $error"
                    }

                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer.startListening(intent)
            } else {
                Toast.makeText(context, "Permissão de áudio negada", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            HeaderBarWithBack(
                title = "Controle Voz",
                onBackClick = onBackClick
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Fale um comando como:")
                Text("➡️ Ligar canal 2\n➡️ Desligar canal 5\n➡️ Desligar todos")

                Button(onClick = {
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }) {
                    Text("Falar agora")
                }

                comandoReconhecido?.let {
                    Text("Último comando: \"$it\"")
                }
            }
        }
    )
}

fun interpretarComando(comando: String, viewModel: RelayViewModel) {
    when {
        comando.contains("ligar canal") -> {
            val canal = comando.filter { it.isDigit() }.toIntOrNull()
            canal?.let {
                viewModel.toggleCanal(it)
            }
        }

        comando.contains("desligar canal") -> {
            val canal = comando.filter { it.isDigit() }.toIntOrNull()
            canal?.let {
                viewModel.toggleCanal(it)
            }
        }

        comando.contains("ligar todos") -> {
            for (i in 0..15) {
                viewModel.toggleCanal(i)
            }
        }

        comando.contains("desligar todos") -> {
            for (i in 0..15) {
                viewModel.toggleCanal(i)
            }
        }

        else -> {
            println("Comando desconhecido: $comando")
        }
    }
}

