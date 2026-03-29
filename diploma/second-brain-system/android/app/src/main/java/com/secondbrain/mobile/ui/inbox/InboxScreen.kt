package com.secondbrain.mobile.ui.inbox

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secondbrain.mobile.AppContainer
import com.secondbrain.mobile.auth.SessionManager
import com.secondbrain.mobile.ui.GenericViewModelFactory
import com.secondbrain.mobile.ui.auth.AdminActionAuthViewModel
import com.secondbrain.mobile.ui.auth.AdminPasswordDialog
import com.secondbrain.mobile.ui.capture.CaptureUiState
import com.secondbrain.mobile.ui.capture.CaptureViewModel
import java.io.File
import java.util.UUID
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    appContainer: AppContainer,
    onNavigateToCapture: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToTopicNote: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: InboxViewModel = viewModel(
        factory = GenericViewModelFactory { InboxViewModel(appContainer.repository) }
    )
    val uiState by viewModel.uiState.collectAsState()

    val captureViewModel: CaptureViewModel = viewModel(
        factory = GenericViewModelFactory { CaptureViewModel(appContainer.repository) }
    )
    val captureUiState by captureViewModel.uiState.collectAsState()

    val authViewModel: AdminActionAuthViewModel = viewModel(
        factory = GenericViewModelFactory { AdminActionAuthViewModel(appContainer.repository) }
    )
    val authLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.errorMessage.collectAsState()
    val authAuthorized by authViewModel.isAuthorized.collectAsState()

    var showAdminDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var quickNoteText by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        val tempFile = copyUriToTempFile(context, uri) ?: return@rememberLauncherForActivityResult
        val sourceType = resolveSourceType(context, uri)

        val action = {
            captureViewModel.uploadRawItem(
                file = tempFile,
                sourceType = sourceType,
                contentText = quickNoteText
            )
        }

        if (SessionManager.getRole() == "ADMIN") {
            action()
        } else {
            pendingAction = action
            showAdminDialog = true
        }
    }

    LaunchedEffect(captureUiState) {
        if (captureUiState is CaptureUiState.Success) {
            quickNoteText = ""
            viewModel.loadData()
        }
    }

    LaunchedEffect(authAuthorized) {
        if (authAuthorized) {
            val actionToExecute = pendingAction
            showAdminDialog = false
            authViewModel.reset()
            pendingAction = null
            
            actionToExecute?.invoke()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Входящие записи",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                HorizontalDivider()

                if (uiState.drawerItems.isEmpty()) {
                    Text(
                        text = "Пока пусто",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(uiState.drawerItems) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { drawerState.close() }
                                        onNavigateToDetails(item.id)
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.createdAt,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Темы") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Входящие записи")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                        }
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Выход")
                        }
                    }
                )
            },
            bottomBar = {
                CaptureBottomBar(
                    value = quickNoteText,
                    onValueChange = { quickNoteText = it },
                    isSaving = captureUiState is CaptureUiState.Loading,
                    onAddClick = {
                        filePickerLauncher.launch("*/*")
                    },
                    onSubmit = {
                        val text = quickNoteText.trim()
                        if (text.isNotEmpty()) {
                            val action = { captureViewModel.createRawItem(text) }
                            if (SessionManager.getRole() == "ADMIN") {
                                action()
                            } else {
                                pendingAction = action
                                showAdminDialog = true
                            }
                        }
                    }
                )
            }
        ) { padding ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "Неизвестная ошибка",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 120.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.topics.isEmpty()) {
                            item {
                                Text(
                                    text = "Темы пока не найдены. Добавь материалы во входящие и подтверди предложения.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(uiState.topics) { topic ->
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToTopicNote(topic.id) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = topic.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Упоминаний: ${topic.mentions}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptureBottomBar(
    value: String,
    onValueChange: (String) -> Unit,
    isSaving: Boolean,
    onAddClick: () -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить файл",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text("Заметка") },
                singleLine = false,
                minLines = 1,
                maxLines = 8,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSubmit() })
            )

            Spacer(modifier = Modifier.size(12.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !isSaving) { onSubmit() },
                contentAlignment = Alignment.Center
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Отправить",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

private fun copyUriToTempFile(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri))
            ?.let { ".$it" }
            ?: ""

        val tempFile = File(context.cacheDir, "upload_${UUID.randomUUID()}$extension")

        contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        tempFile
    } catch (_: Exception) {
        null
    }
}

private fun resolveSourceType(context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri).orEmpty()

    return when {
        mimeType.startsWith("image/") -> "IMAGE"
        mimeType.startsWith("audio/") -> "AUDIO"
        else -> "FILE"
    }
}
