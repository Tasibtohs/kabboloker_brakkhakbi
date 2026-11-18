package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.GroupEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AssignGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.CreateGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteCardItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel

@Composable
fun GroupsScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit = {},
    onOpenEditor: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val groups by viewModel.allGroups.collectAsState()
    val allNotes by viewModel.allNotes.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingGroup by remember { mutableStateOf<GroupEntity?>(null) }
    var selectedGroupDetail by remember { mutableStateOf<GroupEntity?>(null) }
    var noteToMoveGroup by remember { mutableStateOf<NoteEntity?>(null) }

    // System Back Handler
    BackHandler {
        if (selectedGroupDetail != null) {
            selectedGroupDetail = null
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            if (selectedGroupDetail != null) {
                AppTopBar(
                    title = selectedGroupDetail!!.name,
                    subtitle = "গ্রুপের সব নোট ও কবিতা",
                    navigationIcon = {
                        IconButton(onClick = { selectedGroupDetail = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Groups",
                                tint = AmberAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            } else {
                AppTopBar(
                    title = "গ্রুপ ম্যানেজার",
                    subtitle = "কবিতাগুলোকে ফোল্ডার অনুযায়ী বিন্যস্ত করুন",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = AmberAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (selectedGroupDetail == null) {
                FloatingActionButton(
                    onClick = {
                        editingGroup = null
                        showCreateDialog = true
                    },
                    shape = CircleShape,
                    containerColor = GoldPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Group")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (selectedGroupDetail != null) {
                // Showing Notes in selected group
                val groupNotes = allNotes.filter { it.groupId == selectedGroupDetail!!.id && !it.isTrashed && !it.isHidden }

                if (groupNotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "Empty Group",
                                tint = GoldPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "এই গ্রুপে কোনো নোট বা কবিতা নেই",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "হোম স্ক্রিন থেকে নোট সিলেক্ট করে এই গ্রুপে যোগ করুন।",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groupNotes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                isSelected = false,
                                isInSelectionMode = false,
                                groupName = null,
                                onClick = { onOpenEditor(note.id) },
                                onLongClick = {},
                                onTogglePin = { viewModel.togglePinNote(note) },
                                onToggleLock = { viewModel.toggleLockNote(note) },
                                onDelete = { viewModel.softDeleteNote(note.id) },
                                onRemoveFromGroup = { viewModel.removeNoteFromGroup(note) },
                                onMoveToGroup = { noteToMoveGroup = note }
                            )
                        }
                    }
                }
            } else {
                // Showing Groups List
                if (groups.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = "Empty Groups",
                                tint = GoldPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "কোনো গ্রুপ তৈরি করা হয়নি",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Text(
                                text = "নতুন গ্রুপ যোগ করতে '+' বাটনে চাপ দিন",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(groups, key = { it.id }) { group ->
                            val groupNoteCount = allNotes.count { it.groupId == group.id && !it.isTrashed && !it.isHidden }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    .clickable { selectedGroupDetail = group },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(GoldPrimary.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Folder,
                                                contentDescription = "Folder",
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = group.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "$groupNoteCount টি কবিতা/নোট",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = {
                                            editingGroup = group
                                            showCreateDialog = true
                                        }) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = GoldPrimary)
                                        }

                                        IconButton(onClick = { viewModel.deleteGroup(group) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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

    if (showCreateDialog) {
        CreateGroupDialog(
            initialName = editingGroup?.name ?: "",
            title = if (editingGroup == null) "নতুন গ্রুপ তৈরি করুন" else "গ্রুপ রিনেম করুন",
            onConfirm = { name ->
                viewModel.saveGroup(name = name, id = editingGroup?.id ?: 0L)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    noteToMoveGroup?.let { targetNote ->
        AssignGroupDialog(
            groups = groups,
            currentGroupId = targetNote.groupId,
            title = "অন্য গ্রুপে নিন",
            onCreateGroup = {
                editingGroup = null
                showCreateDialog = true
            },
            onSelectGroup = { groupId ->
                viewModel.moveNoteToGroup(targetNote, groupId)
                val targetName = groups.find { it.id == groupId }?.name
                val msg = if (targetName != null) "‘$targetName’ গ্রুপে নেওয়া হয়েছে" else "গ্রুপ থেকে সরানো হয়েছে"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                noteToMoveGroup = null
            },
            onDismiss = { noteToMoveGroup = null }
        )
    }
}
