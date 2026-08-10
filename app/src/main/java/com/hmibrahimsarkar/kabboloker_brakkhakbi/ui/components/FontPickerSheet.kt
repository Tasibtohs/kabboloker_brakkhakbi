package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFontOption
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.BottomSheetShape
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontPickerSheet(
    sheetState: SheetState,
    selectedFontKey: String,
    onFontSelected: (fontKey: String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সব") }
    var currentFontKey by remember { mutableStateOf(selectedFontKey) }

    val categories = listOf("সব", "ক্যালিগ্রাফি", "আর্টিস্টিক", "ডেকোরেটিভ")

    val filteredFonts = BengaliFonts.fonts.filter { font ->
        val matchesCategory = if (selectedCategory == "সব") true else font.category == selectedCategory
        val matchesSearch = font.name.contains(searchQuery, ignoreCase = true) || font.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val activeFontOption = BengaliFonts.getFontByKey(currentFontKey)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetShape,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "বাংলা টাইপোগ্রাফি ফন্ট নির্বাচন",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

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
                    ) {
                        Text(
                            text = category,
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
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filteredFonts) { index, font ->
                    val fontOption = BengaliFonts.getFontByKey(font.key)
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
                                },
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = font.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFontSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = font.category,
                                            fontSize = 10.sp,
                                            color = GoldPrimary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(GoldPrimary.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "কবিতার ক্যানভাসে স্বপ্নের আল্পনা",
                                        fontSize = 15.sp,
                                        fontFamily = fontOption.fontFamily,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
