package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AssignGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.CreateGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteCardItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel

@Composable
fun LockedNotesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    onOpenEditor: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val lockedNotes by viewModel.lockedNotes.collectAsState()
    val allGroups by viewModel.allGroups.collectAsState()

    var noteToMoveGroup by remember { mutableStateOf<NoteEntity?>(null) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "লক করা নোট",
                subtitle = "${lockedNotes.size} টি সুরক্ষিত নোট",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AmberAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("locked_notes_screen")
        ) {
            if (lockedNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "কোনো লক করা নোট নেই",
                            tint = GoldPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো লক করা নোট নেই",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "নোট কার্ড বা এডিটরের লক আইকনে ট্যাপ করে যেকোনো নোট সুরক্ষিত রাখতে পারবেন।",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(lockedNotes, key = { it.id }) { note ->
                        val groupName = allGroups.find { it.id == note.groupId }?.name
                        NoteCardItem(
                            note = note,
                            isSelected = false,
                            isInSelectionMode = false,
                            groupName = groupName,
                            onClick = { onOpenEditor(note.id) },
                            onLongClick = {},
                            onTogglePin = { viewModel.togglePinNote(note) },
                            onToggleLock = {
                                viewModel.toggleLockNote(note)
                                Toast.makeText(context, "নোটের লক খোলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                Toast.makeText(context, "লক করা নোট ডিলিট করা যাবে না", Toast.LENGTH_SHORT).show()
                            },
                            onMoveToGroup = { noteToMoveGroup = note }
                        )
                    }
                }
            }
        }
    }

    noteToMoveGroup?.let { targetNote ->
        AssignGroupDialog(
            groups = allGroups,
            currentGroupId = targetNote.groupId,
            title = "অন্য গ্রুপে নিন",
            onCreateGroup = { showCreateGroupDialog = true },
            onSelectGroup = { groupId ->
                viewModel.moveNoteToGroup(targetNote, groupId)
                val targetName = allGroups.find { it.id == groupId }?.name
                val msg = if (targetName != null) "‘$targetName’ গ্রুপে নেওয়া হয়েছে" else "গ্রুপ থেকে সরানো হয়েছে"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                noteToMoveGroup = null
            },
            onDismiss = { noteToMoveGroup = null }
        )
    }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            onConfirm = { name ->
                viewModel.saveGroup(name = name)
                showCreateGroupDialog = false
            },
            onDismiss = { showCreateGroupDialog = false }
        )
    }
}
