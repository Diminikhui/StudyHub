package com.secondbrain.mobile.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secondbrain.mobile.AppContainer
import com.secondbrain.mobile.model.UiAnswerSourceResponse
import com.secondbrain.mobile.ui.GenericViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    appContainer: AppContainer,
    onBack: () -> Unit
) {
    val viewModel: SearchViewModel = viewModel(
        factory = GenericViewModelFactory { SearchViewModel(appContainer.repository) }
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Спросить систему") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = viewModel::updateQuery,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Введите вопрос") },
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.search() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Спросить")
                    }
                }
            }

            uiState.error?.let {
                item {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            if (uiState.hasSearched && !uiState.isLoading) {
                uiState.answer?.let { answer ->
                    item {
                        SearchStatus(answer.status)
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = answer.answer,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Источники",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (answer.sources.isNotEmpty()) {
                        items(answer.sources) { source ->
                            SourceCard(source)
                        }
                    } else {
                        item {
                            Text(
                                text = "Источники не найдены",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchStatus(status: String) {
    val text = when (status) {
        "GROUNDED" -> "Надёжный ответ"
        "PARTIAL" -> "Частичный ответ"
        "NOT_FOUND" -> "Ничего надёжного не найдено"
        else -> status
    }

    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SourceCard(source: UiAnswerSourceResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = source.entityType,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = source.sourceText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}