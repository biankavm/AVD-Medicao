package com.avd.powertrack.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.avd.powertrack.router.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val consumoEnergiaViewModel = LembrarConsumoEnergia()
    val consumo by consumoEnergiaViewModel.consumoEnergia.collectAsState()
    val calculando by consumoEnergiaViewModel.calculando.collectAsState()

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

            if (!calculando) {
                Button(onClick = { consumoEnergiaViewModel.iniciarCalculo(context) }) {
                    Text("Iniciar Consumo de Energia")
                }
            } else {
                Button(onClick = { consumoEnergiaViewModel.pararCalculo() }) {
                    Text("Parar Consumo de Energia")
                }
            }

            Text("Consumo de Energia: $consumo W")



        }
    }
}

// função para atualizar a interface com o nome do arquivo selecionado
private fun updateUIWithSelectedFileName(fileName: String?) {
    // imprimir o nome do arquivo no logcat
    fileName?.let {
        Log.i("debug", "Arquivo selecionado: $it") // mostra no logcat
    }
}

private fun getLastSelectedFileName(): String? {
    // logica para obter o nome do último arquivo selecionado
    return null // nome do arquivo, a fazer ainda
}

class ConsumoEnergiaViewModel(private val context: Context) : ViewModel() {
    private val _consumoEnergia = MutableStateFlow(0.0f)
    val consumoEnergia = _consumoEnergia.asStateFlow()

    private val _calculando = MutableStateFlow(false)
    val calculando = _calculando.asStateFlow()

    private val intentFilterBattery = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    private var jobCalculo: Job? = null

    fun iniciarCalculo(context: Context) {
        _calculando.value = true
        jobCalculo= viewModelScope.launch {
            while (calculando.value) {
                val inicio = SystemClock.elapsedRealtime()
                val corrente = obterCorrente()
                val tensao = obterTensao(context)

                delay(1000) // Intervalo de 1 segundoo - ajuste conforme necessário

                val fim = SystemClock.elapsedRealtime()
                val deltaT = fim - inicio
                val consumo = calcularConsumo(corrente, tensao, deltaT)
                _consumoEnergia.value = consumo
            }
        }
    }

    fun pararCalculo() {
        _calculando.value = false
        jobCalculo?.cancel()
    }

    private fun obterCorrente(): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
    }

    private fun obterTensao(context: Context): Int {
        val intent = context.registerReceiver(null, intentFilterBattery)
        return intent!!.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
    }

    private fun calcularConsumo(corrente: Int, tensao: Int, duracao: Long): Float {
        val correnteA = (corrente * -1).toFloat() / 1000000
        val tensaoV = tensao.toFloat() / 1000
        val deltaT = duracao.toFloat() / 1000
        return correnteA * tensaoV * deltaT
    }
}

@Composable
fun ConsumoEnergiaTela(viewModel: ConsumoEnergiaViewModel) {
    val consumo by viewModel.consumoEnergia.collectAsState()
    Text("Consumo de Energia: $consumo W")
}

@Composable
fun LembrarConsumoEnergia(): ConsumoEnergiaViewModel {
    val context = LocalContext.current
    return remember {
        ConsumoEnergiaViewModel(context)
    }
}
