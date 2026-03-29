package com.secondbrain.mobile.ui.ask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.secondbrain.mobile.model.UiAnswerSourceResponse

@Composable
fun AskScreen(
    viewModel: AskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Спросить систему",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Введите запрос") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.ask(query) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Спросить")
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = uiState) {
            AskUiState.Idle -> {
                Text("Введите вопрос о ваших материалах.")
            }

            AskUiState.Loading -> {
                CircularProgressIndicator()
            }

            is AskUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is AskUiState.Success -> {
                AnswerContent(
                    answer = state.response.answer,
                    status = state.response.status,
                    sources = state.response.sources
                )
            }
        }
    }
}

@Composable
private fun AnswerContent(
    answer: String,
    status: String,
    sources: List<UiAnswerSourceResponse>
) {
    Text(
        text = when (status) {
            "GROUNDED" -> "Надёжный ответ"
            "PARTIAL" -> "Частичный ответ"
            "NOT_FOUND" -> "Ничего надёжного не найдено"
            else -> status
        },
        style = MaterialTheme.typography.labelLarge
    )

    Spacer(modifier = Modifier.height(12.dp))

    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = answer,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Источники",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sources) { source ->
            SourceCard(source)
        }
    }
}

@Composable
private fun SourceCard(source: UiAnswerSourceResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = source.entityType,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = source.sourceText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}