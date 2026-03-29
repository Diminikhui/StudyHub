package com.secondbrain.mobile.ui.personnote

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.secondbrain.mobile.model.NoteActionItemResponse
import com.secondbrain.mobile.model.NoteFactResponse
import com.secondbrain.mobile.model.NoteTopicResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonNoteScreen(
    viewModel: PersonNoteViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Человек") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            PersonNoteUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PersonNoteUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(state.message)
                }
            }

            is PersonNoteUiState.Success -> {
                val note = state.note

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = note.name,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(note.summary)
                        }
                    }

                    item { SectionTitle("Topics") }
                    if (note.topics.isNotEmpty()) {
                        items(note.topics) { topic ->
                            PersonTopicCard(topic)
                        }
                    } else {
                        item { Text("No topics") }
                    }

                    item { SectionTitle("Facts") }
                    if (note.facts.isNotEmpty()) {
                        items(note.facts) { fact ->
                            PersonFactCard(fact)
                        }
                    } else {
                        item { Text("No facts") }
                    }

                    item { SectionTitle("Actions") }
                    if (note.actions.isNotEmpty()) {
                        items(note.actions) { action ->
                            PersonActionCard(action)
                        }
                    } else {
                        item { Text("No actions") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PersonTopicCard(topic: NoteTopicResponse) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(topic.name)
        }
    }
}

@Composable
private fun PersonFactCard(fact: NoteFactResponse) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(fact.text)
        }
    }
}

@Composable
private fun PersonActionCard(action: NoteActionItemResponse) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            action.topicName?.let {
                SuggestionChip(
                    onClick = {},
                    label = { Text(it) },
                    enabled = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(action.displayText)
        }
    }
}