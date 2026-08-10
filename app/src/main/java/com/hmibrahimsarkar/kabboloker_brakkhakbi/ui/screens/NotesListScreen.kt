package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource
import com.hmibrahimsarkar.kabboloker_brakkhakbi.R
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.GroupEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.VisibilityOff
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AssignGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.DeleteConfirmationDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.InfoBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteCardItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.PasswordPromptDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldGlow
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.LightTextPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel

@Composable
fun NotesListScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    onOpenEditor: (noteId: Long?) -> Unit
) {
    val notes by viewModel.notesList.collectAsState()
    val noteCount by viewModel.activeNoteCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val allGroups by viewModel.allGroups.collectAsState()
    val selectedNoteIds by viewModel.selectedNoteIds.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val savedPasswordHash by viewModel.appPasswordHash.collectAsState()

    val context = LocalContext.current
    var isGridView by remember { mutableStateOf(false) }
    var showAssignGroupDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmModal by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

    // Locked note prompt
    var pendingLockedNote by remember { mutableStateOf<NoteEntity?>(null) }
    var passwordErrorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "কাব্যলোকের ব্রক্ষকবি",
                subtitle = "কবি ও শব্দের আসর",
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Open Drawer",
                            tint = AmberAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                actions = {
                    val gridIconRotation by animateFloatAsState(
                        targetValue = if (isGridView) 180f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "gridIconRotation"
                    )
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Outlined.ViewList else Icons.Outlined.GridView,
                            contentDescription = if (isGridView) "List View" else "Grid View",
                            tint = AmberAccent,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(gridIconRotation)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDarkMode(!(isDarkMode ?: false)) }) {
                        Icon(
                            imageVector = if (isDarkMode == true) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = AmberAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedNoteIds.isEmpty()) {
                // Golden circular FAB with linear gradient and subtle glow shadow
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            spotColor = GoldPrimary,
                            ambientColor = GoldGlow
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GoldLight, GoldPrimary, GoldDark)
                            )
                        )
                        .clickable { onOpenEditor(null) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Poem Note",
                        tint = LightTextPrimary,
                        modifier = Modifier.size(30.dp)
                    )
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
            // Info Bar (Total Notes Count & Date)
            InfoBar(leftText = "$noteCount টি কবিতা ও নোট")

            // Permanent Search Bar below InfoBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "কবিতা বা শব্দ দিয়ে খুঁজুন...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = AmberAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear Search",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Group Filter Pills
            if (allGroups.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isAllSelected = selectedGroupId == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isAllSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.setSelectedGroupFilter(null) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "সবগুলো",
                                fontSize = 12.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(allGroups) { group ->
                        val isGroupSelected = selectedGroupId == group.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isGroupSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.setSelectedGroupFilter(group.id) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Folder,
                                    contentDescription = "Group",
                                    tint = if (isGroupSelected) Color.White else GoldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = group.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isGroupSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isGroupSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Multi-Select Stylish Action Bar
            AnimatedVisibility(
                visible = selectedNoteIds.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = GoldPrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        // Top info & controls row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: "X টি নির্বাচিত" (clean & light)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${selectedNoteIds.size} টি নির্বাচিত",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Right: "সব সিলেক্ট" text button + Close icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                val isAllSelected = selectedNoteIds.size == notes.size && notes.isNotEmpty()
                                Surface(
                                    onClick = {
                                        if (isAllSelected) {
                                            viewModel.clearSelection()
                                        } else {
                                            viewModel.selectAllNotes(notes.map { it.id })
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.SelectAll,
                                            contentDescription = "Select All",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (isAllSelected) "সব বাতিল" else "সব সিলেক্ট",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.clearSelection() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Close Selection Mode",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Assign Group
                            SelectionActionButton(
                                icon = Icons.Outlined.Folder,
                                label = "গ্রুপ",
                                tint = GoldPrimary,
                                onClick = { showAssignGroupDialog = true }
                            )

                            // Hide Note
                            SelectionActionButton(
                                icon = Icons.Outlined.VisibilityOff,
                                label = "হাইড",
                                tint = AmberAccent,
                                onClick = {
                                    val count = selectedNoteIds.size
                                    viewModel.hideSelectedNotes()
                                    Toast.makeText(context, "$count টি নোট হাইড করা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            )

                            // Delete Note
                            SelectionActionButton(
                                icon = Icons.Outlined.Delete,
                                label = "মুছুন",
                                tint = Color(0xFFE53935),
                                onClick = { showDeleteConfirmModal = true }
                            )
                        }
                    }
                }
            }

            // Empty State illustration or Notes LazyColumn
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Glowing Feather Emblem Icon Container
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(12.dp, CircleShape, spotColor = GoldPrimary, ambientColor = GoldGlow)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            GoldLight.copy(alpha = 0.35f),
                                            GoldPrimary.copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(1.5.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.poetry_feather_icon_1785492347781),
                                contentDescription = "Feather Quill Icon",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val titleText = if (searchQuery.isNotEmpty()) "'$searchQuery' সম্পর্কিত কোনো কবিতা নেই" else "কাব্যলোকের শূন্য ক্যানভাস"
                        Text(
                            text = titleText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = GoldPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val subText = if (searchQuery.isNotEmpty()) {
                            "অন্য কোনো শব্দ বা শিরোনাম দিয়ে আবার সন্ধান করুন।"
                        } else {
                            "এখনো কোনো কবিতা বা সাহিত্য যুক্ত করা হয়নি।\nআপনার অনুভূতির প্রথম কাব্যমালা সাজাতে নিচের বাটনে ট্যাপ করুন।"
                        }
                        Text(
                            text = subText,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onOpenEditor(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Write Poem",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "নতুন কবিতা লিখুন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                AnimatedContent(
                    targetState = isGridView,
                    transitionSpec = {
                        (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                scaleIn(
                                    initialScale = 0.94f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )) togetherWith
                        (fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                scaleOut(
                                    targetScale = 1.06f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ))
                    },
                    label = "LayoutSwitchTransition",
                    modifier = Modifier.fillMaxSize()
                ) { targetIsGrid ->
                    if (targetIsGrid) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 110.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalItemSpacing = 8.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = notes,
                                key = { it.id }
                            ) { note ->
                                val group = allGroups.find { it.id == note.groupId }
                                val isSelected = selectedNoteIds.contains(note.id)

                                NoteCardItem(
                                    note = note,
                                    modifier = Modifier.padding(0.dp),
                                    isSelected = isSelected,
                                    isInSelectionMode = selectedNoteIds.isNotEmpty(),
                                    groupName = group?.name,
                                    onClick = {
                                        if (selectedNoteIds.isNotEmpty()) {
                                            viewModel.toggleNoteSelection(note.id)
                                        } else {
                                            onOpenEditor(note.id)
                                        }
                                    },
                                    onLongClick = {
                                        viewModel.toggleNoteSelection(note.id)
                                    },
                                    onTogglePin = {
                                        viewModel.togglePinNote(note)
                                    },
                                    onToggleLock = {
                                        viewModel.toggleLockNote(note)
                                        val msg = if (note.isLocked) "নোটের লক খোলা হয়েছে" else "নোট লক করা হয়েছে"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = {
                                        noteToDelete = note
                                    }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 4.dp, bottom = 110.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = notes,
                                key = { it.id }
                            ) { note ->
                                val group = allGroups.find { it.id == note.groupId }
                                val isSelected = selectedNoteIds.contains(note.id)

                                NoteCardItem(
                                    note = note,
                                    isSelected = isSelected,
                                    isInSelectionMode = selectedNoteIds.isNotEmpty(),
                                    groupName = group?.name,
                                    onClick = {
                                        if (selectedNoteIds.isNotEmpty()) {
                                            viewModel.toggleNoteSelection(note.id)
                                        } else {
                                            onOpenEditor(note.id)
                                        }
                                    },
                                    onLongClick = {
                                        viewModel.toggleNoteSelection(note.id)
                                    },
                                    onTogglePin = {
                                        viewModel.togglePinNote(note)
                                    },
                                    onToggleLock = {
                                        viewModel.toggleLockNote(note)
                                        val msg = if (note.isLocked) "নোটের লক খোলা হয়েছে" else "নোট লক করা হয়েছে"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = {
                                        noteToDelete = note
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Password Prompt for Locked Note
    if (pendingLockedNote != null) {
        PasswordPromptDialog(
            title = "সুরক্ষিত নোট",
            subtitle = "'${pendingLockedNote!!.title}' দেখতে পাসওয়ার্ড দিন",
            errorText = passwordErrorMsg,
            onConfirm = { inputPass ->
                if (viewModel.themePreferences.verifyPassword(inputPass, savedPasswordHash)) {
                    val targetId = pendingLockedNote!!.id
                    pendingLockedNote = null
                    passwordErrorMsg = null
                    onOpenEditor(targetId)
                } else {
                    passwordErrorMsg = "ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।"
                }
            },
            onDismiss = {
                pendingLockedNote = null
                passwordErrorMsg = null
            }
        )
    }

    // Assign Group Dialog
    if (showAssignGroupDialog) {
        AssignGroupDialog(
            groups = allGroups,
            currentGroupId = null,
            onSelectGroup = { groupId ->
                viewModel.assignSelectedNotesToGroup(groupId)
                showAssignGroupDialog = false
            },
            onDismiss = { showAssignGroupDialog = false }
        )
    }

    // Delete Confirmation Modal for Multi-Select
    if (showDeleteConfirmModal) {
        DeleteConfirmationDialog(
            noteCount = selectedNoteIds.size,
            onConfirm = {
                viewModel.softDeleteSelectedNotes()
                showDeleteConfirmModal = false
            },
            onDismiss = { showDeleteConfirmModal = false }
        )
    }

    // Delete Confirmation Modal for Single Note
    if (noteToDelete != null) {
        DeleteConfirmationDialog(
            noteCount = 1,
            onConfirm = {
                viewModel.softDeleteNote(noteToDelete!!.id)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
        )
    }
}

@Composable
private fun SelectionActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = tint.copy(alpha = 0.12f),
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        }
    }
}
