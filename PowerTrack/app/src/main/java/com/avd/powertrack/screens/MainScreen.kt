package com.avd.powertrack.screens

import com.avd.powertrack.screens.SettingsScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.avd.powertrack.router.Screen

@Composable
fun MainScreen(
    navController: NavController
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally, // centralizar horizontal
            verticalArrangement = Arrangement.Center // centralizar vertical
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Monitoramento de Ações no Celular",
                textAlign = TextAlign.Center // centraliza texto
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // logica do monitoramento
                    navController.navigate(Screen.SettingsScreen.route)
                }
            ) {
                Text(
                    text = "Iniciar Monitoramento"
                )
            }
        }
    }
}
