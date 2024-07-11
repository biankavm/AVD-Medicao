package com.avd.powertrack.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.avd.powertrack.router.Screen

@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current

    // iniciaa o gerenciador de arquivos
    val fileManagerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // trata o arquivo selecionado
            val selectedFileUri = result.data?.data
            val fileName = selectedFileUri?.lastPathSegment ?: "Nenhum arquivo selecionado"

            // att a interface com o nome do arquivo selecionado
            updateUIWithSelectedFileName(fileName)
        }
    }

    Scaffold {
        Column(
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Tela Atual: Monitorando..."
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Último arquivo selecionado: ${getLastSelectedFileName() ?: "Nenhum"}"
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // abre o gerenciador de arquivos para pegar um arquivo
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"  // tipo de arquivo desejado (por enquanto todos)
                    }
                    fileManagerLauncher.launch(intent)
                }
            ) {
                Text(text = "Selecionar Arquivo")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(Screen.MainScreen.route)
                }
            ) {
                Text(text = "Voltar para Tela Principal")
            }
        }
    }
}

// função para atualizar a interface com o nome do arquivo selecionado
private fun updateUIWithSelectedFileName(fileName: String?) {
    // imprimir o nome do arquivo no logcat
    fileName?.let {
        Log.i("debug", "Arquivo selecionado: $it" // mostra no logcat
    }
}

private fun getLastSelectedFileName(): String? {
    // logica para obter o nome do último arquivo selecionado
    return null // nome do arquivo, a fazer ainda
}