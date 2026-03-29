package com.secondbrain.mobile.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secondbrain.mobile.AppContainer
import com.secondbrain.mobile.auth.SessionManager
import com.secondbrain.mobile.model.*
import com.secondbrain.mobile.ui.GenericViewModelFactory
import com.secondbrain.mobile.ui.auth.AdminActionAuthViewModel
import com.secondbrain.mobile.ui.auth.AdminPasswordDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawItemDetailsScreen(
    rawItemId: String,
    appContainer: AppContainer,
    onBack: () -> Unit,
    onNavigateToTopicNote: (Long) -> Unit,
    onNavigateToPersonNote: (Long) -> Unit
) {
    val viewModel: RawItemDetailsViewModel = viewModel(
        factory = GenericViewModelFactory { RawItemDetailsViewModel(appContainer.repository, rawItemId) }
    )
    val uiState by viewModel.uiState.collectAsState()

    val authViewModel: AdminActionAuthViewModel = viewModel(
        factory = GenericViewModelFactory { AdminActionAuthViewModel(appContainer.repository) }
    )
    val authLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.errorMessage.collectAsState()
    val authAuthorized by authViewModel.isAuthorized.collectAsState()

    var showAdminDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(authAuthorized) {
        if (authAuthorized) {
            showAdminDialog = false
            pendingAction?.invoke()
            pendingAction = null
            authViewModel.reset()
        }
    }

    AdminPasswordDialog(
        visible = showAdminDialog,
        isLoading = authLoading,
        errorMessage = authError,
        onDismiss = {
            showAdminDialog = false
            pendingAction = null
            authViewModel.reset()
        },
        onConfirm = { password ->
            authViewModel.confirmAdmin(password)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Запись") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.rawItem == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.error?.let {
                    item {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                uiState.rawItem?.let { rawItem ->
                    item {
                        RawItemHeader(rawItem)
                    }
                }

                val pendingProposals = uiState.proposals.filter { it.status == "PENDING" }

                if (pendingProposals.isNotEmpty()) {
                    item { SectionHeader("Предложения") }
                    items(pendingProposals) { proposal ->
                        ProposalCard(
                            proposal = proposal,
                            onAccept = {
                                val action = { viewModel.acceptProposal(proposal.id) }
                                if (SessionManager.getRole() == "ADMIN") action() else {
                                    pendingAction = action
                                    showAdminDialog = true
                                }
                            },
                            onReject = {
                                val action = { viewModel.rejectProposal(proposal.id) }
                                if (SessionManager.getRole() == "ADMIN") action() else {
                                    pendingAction = action
                                    showAdminDialog = true
                                }
                            }
                        )
                    }
                }

                if (uiState.actions.isNotEmpty()) {
                    item { SectionHeader("Действия") }
                    items(uiState.actions) { actionItem ->
                        ActionCard(
                            action = actionItem,
                            onDone = {
                                val action = { viewModel.markActionDone(actionItem.id) }
                                if (SessionManager.getRole() == "ADMIN") action() else {
                                    pendingAction = action
                                    showAdminDialog = true
                                }
                            }
                        )
                    }
                }

                if (uiState.facts.isNotEmpty()) {
                    item { SectionHeader("Факты") }
                    items(uiState.facts) { fact ->
                        FactCard(fact)
                    }
                }

                if (uiState.topics.isNotEmpty()) {
                    item { SectionHeader("Темы") }
                    items(uiState.topics) { topic ->
                        TopicCard(
                            topic = topic,
                            onClick = { onNavigateToTopicNote(topic.id) }
                        )
                    }
                }

                if (uiState.persons.isNotEmpty()) {
                    item { SectionHeader("Люди") }
                    items(uiState.persons) { person ->
                        PersonCard(
                            person = person,
                            onClick = { onNavigateToPersonNote(person.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun RawItemHeader(item: RawItemResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.contentText ?: fallbackRawItemTitle(item.sourceType),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(translateProcessingState(item.processingState)) },
                    enabled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.createdAt.take(16).replace("T", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun fallbackRawItemTitle(sourceType: String): String {
    return when (sourceType) {
        "IMAGE" -> "Изображение"
        "AUDIO" -> "Аудио"
        "FILE" -> "Файл"
        else -> "Материал"
    }
}

private fun translateProcessingState(state: String): String {
    return when (state) {
        "PENDING" -> "В очереди"
        "PROCESSING" -> "Обработка"
        "PROCESSED" -> "Готово"
        "FAILED" -> "Ошибка"
        else -> state
    }
}

@Composable
fun ProposalCard(
    proposal: ProposalResponse,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = proposalTypeLabel(proposal.proposalType),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            proposal.title?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onReject) {
                    Text("Отклонить", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAccept) {
                    Text("Принять")
                }
            }
        }
    }
}

private fun proposalTypeLabel(type: String): String {
    return when (type) {
        "FACT_CANDIDATE" -> "Факт"
        "TOPIC_CANDIDATE" -> "Тема"
        "PERSON_CANDIDATE" -> "Человек"
        "ACTION_CANDIDATE" -> "Действие"
        else -> type
    }
}

@Composable
fun ActionCard(action: ActionItemResponse, onDone: () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                action.topicName?.let { topicName ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(topicName) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = action.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (action.done) TextDecoration.LineThrough else null
                )
            }

            if (!action.done) {
                IconButton(onClick = onDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Готово",
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun FactCard(fact: FactResponse) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            fact.topicName?.let { topicName ->
                SuggestionChip(
                    onClick = {},
                    label = { Text(topicName) },
                    enabled = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = fact.contentText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fact.createdAt.take(16).replace("T", " "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TopicCard(topic: TopicResponse, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = topic.createdAt.take(16).replace("T", " "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PersonCard(person: PersonResponse, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = person.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = person.createdAt.take(16).replace("T", " "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
