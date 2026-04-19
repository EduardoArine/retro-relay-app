package com.eduardo.retro_relay.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun ImageSelectorGrid(
    imagensCDN: List<String>,
    imagensLocais: List<String>,
    onImagemSelecionada: (String) -> Unit,
    onNovaImagemSelecionada: (String) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val input = context.contentResolver.openInputStream(uri)
            val fileName = "console_custom_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, fileName)
            input?.use { file.outputStream().use { out -> out.write(input.readBytes()) } }
            onNovaImagemSelecionada(file.absolutePath)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selecione uma imagem:", color = Color.White)

        LazyRow(modifier = Modifier.padding(top = 12.dp)) {
            items(imagensCDN + imagensLocais) { caminho ->
                val request = if (caminho.startsWith("http")) {
                    ImageRequest.Builder(context).data(caminho).build()
                } else {
                    ImageRequest.Builder(context).data(File(caminho)).build()
                }

                AsyncImage(
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 8.dp)
                        .background(Color.DarkGray)
                        .clickable { onImagemSelecionada(caminho) }
                )
            }
        }

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Selecionar imagem personalizada")
        }
    }
}
