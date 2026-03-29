package com.secondbrain.mobile.ui.capture

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secondbrain.mobile.AppContainer
import com.secondbrain.mobile.ui.GenericViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    appContainer: AppContainer,
    onBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val viewModel: CaptureViewModel = viewModel(
        factory = GenericViewModelFactory { CaptureViewModel(appContainer.repository) }
    )
    val uiState by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is CaptureUiState.Success) {
            onNavigateToDetails((uiState as CaptureUiState.Success).rawItemId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Захват") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Что у вас на уме?") },
                placeholder = { Text("Введите текст...") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState is CaptureUiState.Error) {
                Text(
                    text = (uiState as CaptureUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { viewModel.createRawItem(text) },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank() && uiState !is CaptureUiState.Loading
            ) {
                if (uiState is CaptureUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Сохранить")
                }
            }
        }
    }
}
