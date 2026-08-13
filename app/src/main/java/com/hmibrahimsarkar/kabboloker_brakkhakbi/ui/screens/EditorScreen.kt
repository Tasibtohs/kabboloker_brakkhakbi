package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.DeleteConfirmationDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.ColorPickerSheet
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.FontPickerSheet
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.FormattingDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.getBengaliFullDateTime
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.InfoBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldGlow
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.resolveAdaptiveTextColor
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.resolveAdaptiveTitleColor
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.EditorViewModel
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Long?,
    mainViewModel: MainViewModel,
    editorViewModel: EditorViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val noteState by editorViewModel.noteState.collectAsState()
    val isSaved by editorViewModel.isSaved.collectAsState()
    val canUndo by editorViewModel.canUndo.collectAsState()
    val canRedo by editorViewModel.canRedo.collectAsState()

    val exportPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            mainViewModel.exportNoteToPdfToUri(context, noteState, uri)
        }
    }

    val editorTopBarName by mainViewModel.editorTopBarName.collectAsState()

    var isReadingMode by remember { mutableStateOf(false) }
    var isOverflowMenuExpanded by remember { mutableStateOf(false) }

    // Bottom Sheet States
    var showFormattingSheet by remember { mutableStateOf(false) }
    var showFontSheet by remember { mutableStateOf(false) }
    var showColorSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formatSheetState = rememberModalBottomSheetState()
    val fontSheetState = rememberModalBottomSheetState()
    val colorSheetState = rememberModalBottomSheetState()

    val defaultFontSizeKey by mainViewModel.defaultFontSizeKey.collectAsState()
    val defaultFontFamilyKey by mainViewModel.defaultFontFamilyKey.collectAsState()
    val defaultTextAlignKey by mainViewModel.defaultTextAlignKey.collectAsState()

    val defaultFontSizeSp = when (defaultFontSizeKey) {
        "small" -> 14f
        "large" -> 22f
        else -> 18f
    }

    LaunchedEffect(noteId) {
        editorViewModel.loadNote(
            noteId = noteId,
            defaultFontSizeSp = defaultFontSizeSp,
            defaultFontFamilyKey = defaultFontFamilyKey,
            defaultTextAlign = defaultTextAlignKey
        )
    }

    val titleColor = resolveAdaptiveTitleColor(noteState.titleColorHex)
    val textColor = resolveAdaptiveTextColor(noteState.textColorHex)

    val selectedFontOption = BengaliFonts.getFontByKey(noteState.fontFamilyKey)

    val alignValue = when (noteState.textAlign) {
        "CENTER" -> TextAlign.Center
        "RIGHT" -> TextAlign.Right
        else -> TextAlign.Left
    }

    val textDecorationValue = when {
        noteState.isUnderline && noteState.isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
        noteState.isUnderline -> TextDecoration.Underline
        noteState.isStrikethrough -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    val scrollState = rememberScrollState()

    // Auto-scroll when typing near the bottom so newly typed text is visible above keyboard
    LaunchedEffect(noteState.content, noteState.title) {
        if (scrollState.maxValue > 0) {
            val isNearBottom = scrollState.value >= (scrollState.maxValue - 400)
            if (isNearBottom || noteState.content.length <= 100) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    BackHandler(enabled = isReadingMode) {
        isReadingMode = false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!isReadingMode) {
                AppTopBar(
                    title = editorTopBarName.ifBlank { "কাব্যলোকের ব্রক্ষকবি" },
                    subtitle = if (noteState.isLocked) "🔒 শুধু পঠনযোগ্য" else (if (isSaved) "" else "অটো-সেভ হচ্ছে..."),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (!noteState.isLocked) {
                                    editorViewModel.saveNote()
                                }
                                onBack()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GoldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    actions = {
                        if (!noteState.isLocked) {
                            AnimatedVisibility(
                                visible = canUndo,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(onClick = { editorViewModel.undo() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Undo",
                                        tint = GoldPrimary
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = canRedo,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(onClick = { editorViewModel.redo() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Redo,
                                        contentDescription = "Redo",
                                        tint = GoldPrimary
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { isOverflowMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More Options",
                                tint = GoldPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = isOverflowMenuExpanded,
                            onDismissRequest = { isOverflowMenuExpanded = false },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("রিডিং মোড (পঠন এলাকা)", fontWeight = FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.AutoStories,
                                        contentDescription = "Reading Mode",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowMenuExpanded = false
                                    isReadingMode = true
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            if (noteState.isLocked) {
                                DropdownMenuItem(
                                    text = { Text("সম্পাদনার জন্য আনলক করুন", fontWeight = FontWeight.Medium) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = "Unlock",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    onClick = {
                                        isOverflowMenuExpanded = false
                                        editorViewModel.toggleLock()
                                    }
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }

                            DropdownMenuItem(
                                text = { Text("শেয়ার করুন", fontWeight = FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Share",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowMenuExpanded = false
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, noteState.title)
                                        val authorSignature = editorTopBarName.ifBlank { "কাব্যলোকের ব্রক্ষকবি" }
                                        putExtra(Intent.EXTRA_TEXT, "${noteState.title}\n\n${noteState.content}\n\n— $authorSignature")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            DropdownMenuItem(
                                text = { Text("অনুলিপি বা কপি", fontWeight = FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowMenuExpanded = false
                                    clipboardManager.setText(AnnotatedString("${noteState.title}\n\n${noteState.content}"))
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            DropdownMenuItem(
                                text = { Text("এই নোট PDF করুন", fontWeight = FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.PictureAsPdf,
                                        contentDescription = "PDF Export",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowMenuExpanded = false
                                    val noteTitle = if (noteState.title.isNotBlank()) noteState.title else "শিরোনামহীন_কবিতা"
                                    val sanitizedTitle = noteTitle.take(30).replace(Regex("[\\\\/:*?\"<>|]"), "_")
                                    val defaultFileName = "${sanitizedTitle}.pdf"
                                    exportPdfLauncher.launch(defaultFileName)
                                }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            DropdownMenuItem(
                                text = { Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowMenuExpanded = false
                                    editorViewModel.deleteCurrentNote()
                                    onBack()
                                }
                            )
                        }
                    }
                )
            } else {
                // Reading Mode AppTopBar
                AppTopBar(
                    title = editorTopBarName.ifBlank { "কাব্যলোকের ব্রক্ষকবি" },
                    subtitle = "📖 পঠন এলাকা",
                    navigationIcon = {
                        IconButton(onClick = { isReadingMode = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Exit Reading Mode",
                                tint = GoldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(50))
                                .clickable { isReadingMode = false }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "সম্পাদনা করুন",
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সম্পাদনা",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {},
        bottomBar = {
            if (!isReadingMode && !noteState.isLocked) {
                // Pill Shape Bottom Formatting Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Aa (Formatting)
                        IconButton(
                            onClick = { showFormattingSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TextFields,
                                contentDescription = "Formatting",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Font
                        IconButton(
                            onClick = { showFontSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FontDownload,
                                contentDescription = "Font Picker",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Color
                        IconButton(
                            onClick = { showColorSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ColorLens,
                                contentDescription = "Color Picker",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Lock
                        IconButton(
                            onClick = { editorViewModel.updateStyle(isLocked = !noteState.isLocked) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (noteState.isLocked) Icons.Filled.Lock else Icons.Outlined.Lock,
                                contentDescription = "Lock",
                                tint = if (noteState.isLocked) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Pin
                        IconButton(
                            onClick = { editorViewModel.updateStyle(isPinned = !noteState.isPinned) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (noteState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Pin",
                                tint = if (noteState.isPinned) AmberAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Save (Checkmark)
                        IconButton(
                            onClick = {
                                scope.launch {
                                    editorViewModel.saveNote()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Save Note",
                                tint = GoldPrimary
                            )
                        }
                    }
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
            // Date Row: thin row below top bar with light gray bg & subtle border, centered Bengali date-time
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getBengaliFullDateTime(noteState.updatedAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Content Area with generous left-right padding (18.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                val titleFontSize = if (isReadingMode) 24.sp else 22.sp
                val readingFontSizeSp = maxOf(20f, noteState.fontSizeSp + 2f)
                val effectiveFontSizeSp = if (isReadingMode) readingFontSizeSp else noteState.fontSizeSp
                val effectiveLineMultiplier = if (isReadingMode) maxOf(noteState.lineSpacingMultiplier * 1.4f, 1.8f) else (noteState.lineSpacingMultiplier * 1.3f)
                val effectiveLineHeightSp = (effectiveFontSizeSp * effectiveLineMultiplier).sp

                // Title Field: plain text field, medium-large font size, light gray placeholder "শিরোনাম...", no border/underline
                BasicTextField(
                    value = noteState.title,
                    onValueChange = { editorViewModel.updateTitle(it) },
                    readOnly = isReadingMode || noteState.isLocked,
                    textStyle = TextStyle(
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = selectedFontOption.fontFamily,
                        color = titleColor,
                        textAlign = alignValue
                    ),
                    cursorBrush = SolidColor(AmberAccent),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (noteState.title.isEmpty()) {
                            Text(
                                text = "শিরোনাম...",
                                fontSize = titleFontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = selectedFontOption.fontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                textAlign = alignValue
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Content Field: directly below title, placeholder "আপনার কবিতা বা লেখা লিখুন...", small light gray text, multiline
                BasicTextField(
                    value = noteState.content,
                    onValueChange = { editorViewModel.updateContent(it) },
                    readOnly = isReadingMode || noteState.isLocked,
                    textStyle = TextStyle(
                        fontSize = effectiveFontSizeSp.sp,
                        fontWeight = if (noteState.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (noteState.isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = textDecorationValue,
                        fontFamily = selectedFontOption.fontFamily,
                        color = textColor,
                        textAlign = alignValue,
                        lineHeight = effectiveLineHeightSp
                    ),
                    cursorBrush = SolidColor(AmberAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp),
                    decorationBox = { innerTextField ->
                        if (noteState.content.isEmpty()) {
                            Text(
                                text = "আপনার কবিতা বা লেখা লিখুন...",
                                fontSize = 15.sp,
                                fontFamily = selectedFontOption.fontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                textAlign = alignValue
                            )
                        }
                        innerTextField()
                    }
                )

                // Poet/Author footer line
                val authorDisplayName = "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি"
                Spacer(modifier = Modifier.height(28.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .align(if (alignValue == TextAlign.Center) Alignment.CenterHorizontally else if (alignValue == TextAlign.Right) Alignment.End else Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "— লেখক: $authorDisplayName",
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    fontFamily = selectedFontOption.fontFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    textAlign = alignValue,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(180.dp))
            }
        }
    }

    // Bottom Formatting Sheets
    if (showFormattingSheet) {
        FormattingDialog(
            sheetState = formatSheetState,
            isBold = noteState.isBold,
            isItalic = noteState.isItalic,
            isUnderline = noteState.isUnderline,
            isStrikethrough = noteState.isStrikethrough,
            textAlign = noteState.textAlign,
            lineBreakMode = noteState.lineBreakMode,
            fontSizeSp = noteState.fontSizeSp,
            lineSpacingMultiplier = noteState.lineSpacingMultiplier,
            onFormatChanged = { b, i, u, s, align, breakMode, fontSize, spacing ->
                editorViewModel.updateStyle(
                    isBold = b,
                    isItalic = i,
                    isUnderline = u,
                    isStrikethrough = s,
                    textAlign = align,
                    lineBreakMode = breakMode,
                    fontSizeSp = fontSize,
                    lineSpacingMultiplier = spacing
                )
            },
            onDismiss = { showFormattingSheet = false }
        )
    }

    if (showFontSheet) {
        FontPickerSheet(
            sheetState = fontSheetState,
            selectedFontKey = noteState.fontFamilyKey,
            onFontSelected = { key ->
                editorViewModel.updateStyle(fontFamilyKey = key)
            },
            onDismiss = { showFontSheet = false }
        )
    }

    if (showColorSheet) {
        ColorPickerSheet(
            sheetState = colorSheetState,
            initialTitleColorHex = noteState.titleColorHex,
            initialTextColorHex = noteState.textColorHex,
            onColorsSelected = { titleHex, textHex ->
                editorViewModel.updateStyle(titleColorHex = titleHex, textColorHex = textHex)
            },
            onDismiss = { showColorSheet = false }
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            noteCount = 1,
            onConfirm = {
                editorViewModel.deleteCurrentNote()
                showDeleteDialog = false
                onBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
