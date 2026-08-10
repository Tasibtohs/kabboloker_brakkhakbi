package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppDisplayFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.BottomSheetShape
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldGlow
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattingDialog(
    sheetState: SheetState,
    isBold: Boolean,
    isItalic: Boolean,
    isUnderline: Boolean,
    isStrikethrough: Boolean,
    textAlign: String,
    lineBreakMode: String,
    fontSizeSp: Float,
    lineSpacingMultiplier: Float,
    onFormatChanged: (
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        isStrikethrough: Boolean,
        textAlign: String,
        lineBreakMode: String,
        fontSizeSp: Float,
        lineSpacingMultiplier: Float
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var bold by remember { mutableStateOf(isBold) }
    var italic by remember { mutableStateOf(isItalic) }
    var underline by remember { mutableStateOf(isUnderline) }
    var strikethrough by remember { mutableStateOf(isStrikethrough) }
    var align by remember { mutableStateOf(textAlign) }
    var breakMode by remember { mutableStateOf(lineBreakMode) }
    var fontSize by remember { mutableStateOf(fontSizeSp) }
    var spacing by remember { mutableStateOf(lineSpacingMultiplier) }

    var showFontSizeSheet by remember { mutableStateOf(false) }
    var showSpacingSheet by remember { mutableStateOf(false) }
    var showBreakModeSheet by remember { mutableStateOf(false) }

    val optionSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Helper function to broadcast live changes immediately to the note
    fun updateLiveFormat(
        b: Boolean = bold,
        i: Boolean = italic,
        u: Boolean = underline,
        s: Boolean = strikethrough,
        a: String = align,
        bm: String = breakMode,
        fs: Float = fontSize,
        sp: Float = spacing
    ) {
        onFormatChanged(b, i, u, s, a, bm, fs, sp)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetShape,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "টেক্সট ফরম্যাটিং",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppDisplayFont,
                color = GoldPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Gold divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                GoldLight,
                                GoldPrimary,
                                GoldDark,
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview Box
            Text(
                text = "লাইভ প্রিভিউ (পরীক্ষামূলক রিয়েল-টাইম নমুনা)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))

            val previewAlign = when (align) {
                "CENTER" -> TextAlign.Center
                "RIGHT" -> TextAlign.Right
                else -> TextAlign.Left
            }

            val previewDecoration = when {
                underline && strikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                underline -> TextDecoration.Underline
                strikethrough -> TextDecoration.LineThrough
                else -> TextDecoration.None
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "আপনার লেখা এমন দেখাবে — কাব্যলোকের গান",
                        fontSize = fontSize.sp,
                        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = previewDecoration,
                        textAlign = previewAlign,
                        lineHeight = (fontSize * spacing * 1.3f).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Text Style Buttons (B / I / U / S)
            Text(
                text = "টেক্সট স্টাইল",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumFormatToggleButton(
                    icon = Icons.Filled.FormatBold,
                    label = "বোল্ড",
                    isSelected = bold,
                    onClick = {
                        bold = !bold
                        updateLiveFormat(b = bold)
                    },
                    modifier = Modifier.weight(1f)
                )
                PremiumFormatToggleButton(
                    icon = Icons.Filled.FormatItalic,
                    label = "ইটালিক",
                    isSelected = italic,
                    onClick = {
                        italic = !italic
                        updateLiveFormat(i = italic)
                    },
                    modifier = Modifier.weight(1f)
                )
                PremiumFormatToggleButton(
                    icon = Icons.Filled.FormatUnderlined,
                    label = "আন্ডারলাইন",
                    isSelected = underline,
                    onClick = {
                        underline = !underline
                        updateLiveFormat(u = underline)
                    },
                    modifier = Modifier.weight(1f)
                )
                PremiumFormatToggleButton(
                    icon = Icons.Filled.FormatStrikethrough,
                    label = "কাটা",
                    isSelected = strikethrough,
                    onClick = {
                        strikethrough = !strikethrough
                        updateLiveFormat(s = strikethrough)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Text Alignment Selector
            Text(
                text = "সারিবদ্ধকরণ (ALIGNMENT)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            PillAlignmentSelector(
                selectedAlign = align,
                onAlignSelected = { selected ->
                    align = selected
                    updateLiveFormat(a = selected)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Dropdowns for Font Size, Line Spacing & Line Break
            Text(
                text = "কনফিগারেশন ও সাইজিং",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Font Size Button
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ফন্ট সাইজ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelectorBox(
                        text = "${fontSize.toInt()} sp",
                        onClick = { showFontSizeSheet = true }
                    )
                }

                // Line Spacing Button
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "লাইন স্পেসিং",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelectorBox(
                        text = "${spacing}x",
                        onClick = { showSpacingSheet = true }
                    )
                }

                // Line Break Button
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "লাইন ব্রেক",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val breakLabel = when (breakMode) {
                        "WORD" -> "শব্দ"
                        "LINE_BY_LINE" -> "লাইন"
                        else -> "বন্ধ"
                    }
                    DropdownSelectorBox(
                        text = breakLabel,
                        onClick = { showBreakModeSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Golden Gradient "সম্পন্ন" Button
            Button(
                onClick = {
                    onFormatChanged(bold, italic, underline, strikethrough, align, breakMode, fontSize, spacing)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(14.dp), spotColor = GoldGlow),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldLight, GoldPrimary, GoldDark)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "সম্পন্ন",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Modal Option Sheets for Font Size
    if (showFontSizeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFontSizeSheet = false },
            sheetState = optionSheetState,
            shape = BottomSheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "ফন্ট সাইজ নির্বাচন করুন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                listOf(12f, 14f, 16f, 18f, 20f, 22f, 24f, 28f, 32f).forEach { size ->
                    val isSelected = fontSize == size
                    OptionRowItem(
                        title = "${size.toInt()} sp",
                        isSelected = isSelected,
                        onClick = {
                            fontSize = size
                            updateLiveFormat(fs = size)
                            showFontSizeSheet = false
                        }
                    )
                }
            }
        }
    }

    // Modal Option Sheets for Line Spacing
    if (showSpacingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpacingSheet = false },
            sheetState = optionSheetState,
            shape = BottomSheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "লাইন স্পেসিং নির্বাচন করুন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                listOf(1.0f, 1.2f, 1.4f, 1.6f, 1.8f, 2.0f, 2.5f).forEach { mult ->
                    val isSelected = spacing == mult
                    OptionRowItem(
                        title = "${mult}x স্পেসিং",
                        isSelected = isSelected,
                        onClick = {
                            spacing = mult
                            updateLiveFormat(sp = mult)
                            showSpacingSheet = false
                        }
                    )
                }
            }
        }
    }

    // Modal Option Sheets for Line Break
    if (showBreakModeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBreakModeSheet = false },
            sheetState = optionSheetState,
            shape = BottomSheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "অটো লাইন ব্রেক অপশন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                listOf(
                    Pair("WORD", "শব্দ অনুযায়ী স্বয়ংক্রিয় ব্রেক"),
                    Pair("LINE_BY_LINE", "লাইন বাই লাইন ব্রেক"),
                    Pair("OFF", "বন্ধ (ম্যানুয়াল ব্রেক)")
                ).forEach { (modeKey, modeTitle) ->
                    val isSelected = breakMode == modeKey
                    OptionRowItem(
                        title = modeTitle,
                        isSelected = isSelected,
                        onClick = {
                            breakMode = modeKey
                            updateLiveFormat(bm = modeKey)
                            showBreakModeSheet = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumFormatToggleButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnimated by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scaleAnim"
    )

    val contentColorAnimated by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 180),
        label = "colorAnim"
    )

    val borderModifier = if (isSelected) {
        Modifier.border(1.dp, GoldLight, RoundedCornerShape(12.dp))
    } else {
        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    }

    val backgroundModifier = if (isSelected) {
        Modifier.background(
            Brush.horizontalGradient(colors = listOf(GoldLight, GoldPrimary, GoldDark)),
            shape = RoundedCornerShape(12.dp)
        )
    } else {
        Modifier.background(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Box(
        modifier = modifier
            .scale(scaleAnimated)
            .then(backgroundModifier)
            .then(borderModifier)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColorAnimated,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColorAnimated
            )
        }
    }
}

@Composable
fun PillAlignmentSelector(
    selectedAlign: String,
    onAlignSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(
            Triple("LEFT", Icons.Filled.FormatAlignLeft, "বাম"),
            Triple("CENTER", Icons.Filled.FormatAlignCenter, "মাঝ"),
            Triple("RIGHT", Icons.Filled.FormatAlignRight, "ডান")
        ).forEach { (alignKey, icon, label) ->
            val isSelected = selectedAlign == alignKey

            val bgModifier = if (isSelected) {
                Modifier.background(
                    Brush.horizontalGradient(colors = listOf(GoldLight, GoldPrimary, GoldDark)),
                    shape = RoundedCornerShape(50)
                )
            } else {
                Modifier
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(bgModifier)
                    .clip(RoundedCornerShape(50))
                    .clickable { onAlignSelected(alignKey) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownSelectorBox(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun OptionRowItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GoldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
