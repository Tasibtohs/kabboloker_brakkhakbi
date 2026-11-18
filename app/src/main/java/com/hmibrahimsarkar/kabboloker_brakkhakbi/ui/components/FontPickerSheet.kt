package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFontOption
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.BottomSheetShape
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontPickerSheet(
    sheetState: SheetState,
    selectedFontKey: String,
    onFontSelected: (fontKey: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allFonts by BengaliFonts.fontsFlow.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সব") }
    var currentFontKey by remember(selectedFontKey) { mutableStateOf(selectedFontKey) }
    var isImporting by remember { mutableStateOf(false) }
    var importFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var isImportFeedbackError by remember { mutableStateOf(false) }
    var fontPendingDelete by remember { mutableStateOf<BengaliFontOption?>(null) }

    val fontImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNullOrEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            isImporting = true
            try {
                val result = BengaliFonts.importFontsFromUris(context, uris)
                val importedCount = result.imported.size
                val dupCount = result.duplicates.size
                val invalidCount = result.invalid.size

                if (importedCount > 0) {
                    val firstImported = result.imported.first()
                    currentFontKey = firstImported.key
                    // If user is currently filtering a different category, switch to the new font's auto-assigned category
                    if (selectedCategory != "সব" && selectedCategory != firstImported.category) {
                        selectedCategory = firstImported.category
                    }

                    val msg = buildString {
                        if (importedCount == 1) {
                            append("‘${firstImported.name}’ (${firstImported.category}) ইমপোর্ট হয়েছে")
                        } else {
                            append("${importedCount}টি বাংলা ফন্ট অটো-গ্রুপসহ ইমপোর্ট হয়েছে")
                        }
                        if (dupCount > 0) append(" • ${dupCount}টি ডুবলিকেট বাদ")
                        if (invalidCount > 0) append(" • ${invalidCount}টি ফাইল সমর্থিত নয়")
                    }
                    importFeedbackMessage = msg
                    isImportFeedbackError = false
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                } else if (dupCount > 0) {
                    val firstDup = result.duplicates.first()
                    val msg = if (dupCount == 1) {
                        "ডুবলিকেট ফন্ট! ‘${firstDup.existingName}’ ইতিমধ্যেই অ্যাপে রয়েছে।"
                    } else {
                        "সবগুলো ফন্ট ইতিমধ্যেই অ্যাপে রয়েছে (${dupCount}টি ডুবলিকেট)।"
                    }
                    importFeedbackMessage = msg
                    isImportFeedbackError = true
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                } else if (invalidCount > 0) {
                    val msg = "সঠিক .ttf বা .otf বাংলা ফন্ট ফাইল নির্বাচন করুন।"
                    importFeedbackMessage = msg
                    isImportFeedbackError = true
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                isImporting = false
            }
        }
    }

    val categories = listOf("সব", "ক্যালিগ্রাফি", "আর্টিস্টিক", "ডেকোরেটিভ")

    val filteredFonts = remember(allFonts, selectedCategory, searchQuery) {
        allFonts.filter { font ->
            val matchesCategory = if (selectedCategory == "সব") true else font.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                font.name.contains(searchQuery, ignoreCase = true) ||
                font.description.contains(searchQuery, ignoreCase = true) ||
                font.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val activeFontOption = remember(currentFontKey, allFonts) {
        BengaliFonts.getFontByKey(currentFontKey)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetShape,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header Row: Title + Import Font Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বাংলা টাইপোগ্রাফি ফন্ট",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        if (!isImporting) {
                            fontImportLauncher.launch(arrayOf("*/*"))
                        }
                    },
                    enabled = !isImporting,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("import_font_button")
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = GoldPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "বাংলা ফন্ট ইমপোর্ট করুন",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isImporting) "ইমপোর্ট হচ্ছে..." else "ফন্ট ইমপোর্ট",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }
            }

            // Optional Import Feedback Banner
            if (!importFeedbackMessage.isNullOrBlank()) {
                val bannerColor = if (isImportFeedbackError) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
                } else {
                    GoldPrimary.copy(alpha = 0.14f)
                }
                val textColor = if (isImportFeedbackError) {
                    MaterialTheme.colorScheme.error
                } else {
                    GoldPrimary
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bannerColor)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = importFeedbackMessage.orEmpty(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("font_import_feedback")
                    )
                    IconButton(
                        onClick = { importFeedbackMessage = null },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "বার্তা বন্ধ করুন",
                            tint = textColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Fixed Live Preview Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = "লাইভ প্রিভিউ: ${activeFontOption.name} (${activeFontOption.category})",
                        fontSize = 11.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "শব্দের তীরে বসে কবিতার ঢেউ গুনি প্রতিরাতে...",
                        fontSize = 18.sp,
                        fontFamily = activeFontOption.fontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ফন্ট খুঁজুন...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories) { category ->
                    val isCatSelected = selectedCategory == category
                    val count = remember(allFonts, category) {
                        if (category == "সব") allFonts.size else allFonts.count { it.category == category }
                    }
                    val catScale by animateFloatAsState(
                        targetValue = if (isCatSelected) 1.05f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "catScale"
                    )
                    Box(
                        modifier = Modifier
                            .scale(catScale)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isCatSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("font_category_$category")
                    ) {
                        Text(
                            text = "$category ($count)",
                            fontSize = 13.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Font Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredFonts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো ফন্ট পাওয়া যায়নি। চাইলে ‘ফন্ট ইমপোর্ট’ থেকে .ttf বা .otf ফন্ট যোগ করুন।",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(
                        items = filteredFonts,
                        key = { _, item -> item.key }
                    ) { index, font ->
                        val fontOption = remember(font.key, allFonts) {
                            BengaliFonts.getFontByKey(font.key)
                        }
                        val isFontSelected = font.key == currentFontKey
                        val fontCardScale by animateFloatAsState(
                            targetValue = if (isFontSelected) 1.02f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "fontCardScale"
                        )

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetY = { it * (index + 1) / 5 }
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(fontCardScale)
                                    .border(
                                        width = if (isFontSelected) 2.dp else 1.dp,
                                        color = if (isFontSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        currentFontKey = font.key
                                        onFontSelected(font.key)
                                    }
                                    .testTag("font_option_${font.key}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isFontSelected) GoldLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = font.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = if (isFontSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Text(
                                                text = font.category,
                                                fontSize = 10.sp,
                                                color = GoldPrimary,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(GoldPrimary.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                            if (font.isCustom) {
                                                Text(
                                                    text = "ইমপোর্টেড",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "কবিতার ক্যানভাসে স্বপ্নের আল্পনা",
                                            fontSize = 15.sp,
                                            fontFamily = fontOption.fontFamily,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (font.isCustom) {
                                            IconButton(
                                                onClick = { fontPendingDelete = font },
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .testTag("delete_custom_font_${font.key}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.DeleteOutline,
                                                    contentDescription = "ইমপোর্ট করা ফন্ট মুছুন",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        if (isFontSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(GoldPrimary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
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

            Spacer(modifier = Modifier.height(14.dp))
        }
    }

    fontPendingDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { fontPendingDelete = null },
            title = {
                Text(
                    text = "ফন্টটি মুছে ফেলবেন?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "‘${target.name}’ ফন্টটি অ্যাপ থেকে সরানো হবে।",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val keyToRemove = target.key
                        fontPendingDelete = null
                        scope.launch {
                            BengaliFonts.deleteCustomFont(context, keyToRemove)
                            if (currentFontKey == keyToRemove) {
                                currentFontKey = "anupam_mahdi"
                            }
                            Toast.makeText(context, "ফন্ট সরানো হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছে ফেলুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { fontPendingDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
