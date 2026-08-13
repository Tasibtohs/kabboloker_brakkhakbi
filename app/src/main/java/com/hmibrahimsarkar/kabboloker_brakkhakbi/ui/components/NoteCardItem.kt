package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.resolveAdaptiveTextColor
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.resolveAdaptiveTitleColor

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldGlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCardItem(
    note: NoteEntity,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    groupName: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleLock: () -> Unit = {},
    onDelete: () -> Unit = {},
    onRemoveFromGroup: (() -> Unit)? = null,
    onUnhide: (() -> Unit)? = null
) {
    val cardBg = MaterialTheme.colorScheme.surface

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.025f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else (if (note.isPinned) 4f else 2f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardElevation"
    )

    val titleColor = resolveAdaptiveTitleColor(note.titleColorHex)

    val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(18.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Lock Accent Stripe (Left side) if note is locked (hidden in selection mode)
            if (note.isLocked && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFC62828))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header Row (if group badge or remove from group button or unhide button)
                if (groupName != null || onRemoveFromGroup != null || onUnhide != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (groupName != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = "Group",
                                        tint = AmberAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = groupName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AmberAccent
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (onRemoveFromGroup != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                                        .clickable { onRemoveFromGroup() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "✕ গ্রুপ থেকে সরান",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            if (onUnhide != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(AmberAccent.copy(alpha = 0.2f))
                                        .clickable { onUnhide() }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "👁️ আন-হাইড করুন",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Top Row: Title on the left, Pin/Lock badges on the top right corner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val titleText = if (note.title.isNotBlank()) note.title else "শিরোনামহীন"
                    Text(
                        text = titleText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontOption.fontFamily,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Top Right Corner Badges (Pin, Lock & Selection Checkmark)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!isInSelectionMode) {
                            if (note.isLocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (note.isPinned) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Pinned",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (isInSelectionMode || isSelected) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = if (isSelected) "Selected" else "Not Selected",
                                tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Middle: Note Content Preview (or locked indicator)
                if (note.isLocked) {
                    Text(
                        text = "🔒 এই কবিতাটি সুরক্ষিত রয়েছে",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    val previewText = if (note.content.isNotBlank()) note.content else "কোনো লেখা নেই..."
                    Text(
                        text = previewText,
                        fontSize = 14.sp,
                        fontFamily = fontOption.fontFamily,
                        color = resolveAdaptiveTextColor(note.textColorHex).copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Row: Clock + Bengali Date/Time on left, Action Pill on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Clock icon + Bengali date and time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = getBengaliFormattedDateTime(note.updatedAt),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right side: Rounded pill container with 3 icons (Pin, Lock, Delete) - Only if NOT in selection mode
                    if (!isInSelectionMode) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // 1. Pin Icon
                            IconButton(
                                onClick = onTogglePin,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin Note",
                                    tint = if (note.isPinned) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // 2. Lock Icon
                            IconButton(
                                onClick = onToggleLock,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (note.isLocked) Icons.Filled.Lock else Icons.Outlined.Lock,
                                    contentDescription = "Lock Note",
                                    tint = if (note.isLocked) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // 3. Delete Icon
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Note",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Pin Accent Stripe (Right side) if note is pinned (hidden in selection mode)
            if (note.isPinned && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(AmberAccent)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: NoteEntity,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    groupName: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleLock: () -> Unit = {},
    onDelete: () -> Unit = {},
    onRemoveFromGroup: (() -> Unit)? = null,
    onUnhide: (() -> Unit)? = null
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val titleColor = resolveAdaptiveTitleColor(note.titleColorHex)
    val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 0.8.dp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Lock Accent Stripe (Left side) if note is locked (hidden in selection mode)
            if (note.isLocked && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFC62828))
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isInSelectionMode || isSelected) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                        contentDescription = null,
                        tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val titleText = if (note.title.isNotBlank()) note.title else "শিরোনামহীন"
                        Text(
                            text = titleText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontOption.fontFamily,
                            color = titleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (!isInSelectionMode) {
                            if (note.isLocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (note.isPinned) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Pinned",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    val previewText = if (note.isLocked) "🔒 সুরক্ষিত কবিতা" else if (note.content.isNotBlank()) note.content else "কোনো লেখা নেই..."
                    Text(
                        text = previewText,
                        fontSize = 12.sp,
                        fontFamily = fontOption.fontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (groupName != null) {
                            Text(
                                text = "📁 $groupName",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                        }
                        Text(
                            text = getBengaliFormattedDateTime(note.updatedAt),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                if (!isInSelectionMode) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // 1. Pin Icon
                        IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Pin Note",
                                tint = if (note.isPinned) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // 2. Lock Icon
                        IconButton(onClick = onToggleLock, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (note.isLocked) Icons.Filled.Lock else Icons.Outlined.Lock,
                                contentDescription = "Lock Note",
                                tint = if (note.isLocked) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // 3. Delete Icon
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete Note",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Pin Accent Stripe (Right side) if note is pinned (hidden in selection mode)
            if (note.isPinned && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(AmberAccent)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteGridItem(
    note: NoteEntity,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    groupName: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleLock: () -> Unit = {},
    onDelete: () -> Unit = {},
    onRemoveFromGroup: (() -> Unit)? = null,
    onUnhide: (() -> Unit)? = null
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val titleColor = resolveAdaptiveTitleColor(note.titleColorHex)
    val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(14.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Lock Accent Stripe (Left side) if note is locked (hidden in selection mode)
            if (note.isLocked && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFC62828))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                if (groupName != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = groupName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = AmberAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val titleText = if (note.title.isNotBlank()) note.title else "শিরোনামহীন"
                    Text(
                        text = titleText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontOption.fontFamily,
                        color = titleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (!isInSelectionMode) {
                            if (note.isLocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (note.isPinned) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Pinned",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        if (isInSelectionMode || isSelected) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = null,
                                tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val previewText = if (note.isLocked) "🔒 সুরক্ষিত কবিতা" else if (note.content.isNotBlank()) note.content else "কোনো লেখা নেই..."
                Text(
                    text = previewText,
                    fontSize = 12.sp,
                    fontFamily = fontOption.fontFamily,
                    color = resolveAdaptiveTextColor(note.textColorHex).copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getBengaliFormattedDateTime(note.updatedAt),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (!isInSelectionMode) {
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                            // 1. Pin Icon
                            IconButton(onClick = onTogglePin, modifier = Modifier.size(26.dp)) {
                                Icon(
                                    imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin Note",
                                    tint = if (note.isPinned) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // 2. Lock Icon
                            IconButton(onClick = onToggleLock, modifier = Modifier.size(26.dp)) {
                                Icon(
                                    imageVector = if (note.isLocked) Icons.Filled.Lock else Icons.Outlined.Lock,
                                    contentDescription = "Lock Note",
                                    tint = if (note.isLocked) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // 3. Delete Icon
                            IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Note",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Pin Accent Stripe (Right side) if note is pinned (hidden in selection mode)
            if (note.isPinned && !isInSelectionMode) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(AmberAccent)
                )
            }
        }
    }
}

