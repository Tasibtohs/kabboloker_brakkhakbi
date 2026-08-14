package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.FontPickerSheet
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.SetPasswordDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppTitleFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel
import com.hmibrahimsarkar.kabboloker_brakkhakbi.util.PdfExportHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val passwordHash by viewModel.appPasswordHash.collectAsState()
    val fontSizePreference by viewModel.fontSizePreference.collectAsState()
    val currentTopBarName by viewModel.editorTopBarName.collectAsState()
    val currentAuthorSignatureName by viewModel.authorSignatureName.collectAsState()

    // Default Writing Settings
    val defaultFontSizeKey by viewModel.defaultFontSizeKey.collectAsState()
    val defaultFontFamilyKey by viewModel.defaultFontFamilyKey.collectAsState()
    val defaultTextAlignKey by viewModel.defaultTextAlignKey.collectAsState()

    // Reminder Settings
    val isReminderMasterEnabled by viewModel.isReminderMasterEnabled.collectAsState()
    val isDailyReminderEnabled by viewModel.isDailyReminderEnabled.collectAsState()
    val reminderHour by viewModel.reminderHour.collectAsState()
    val reminderMinute by viewModel.reminderMinute.collectAsState()

    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showFontPickerSheet by remember { mutableStateOf(false) }
    val fontSheetState = rememberModalBottomSheetState()

    var showPermissionDeniedBanner by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showPermissionDeniedBanner = false
            viewModel.setReminderMasterEnabled(context, true)
            Toast.makeText(context, "নোটিফিকেশন পারমিশন ও রিমাইন্ডার চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
        } else {
            showPermissionDeniedBanner = true
            Toast.makeText(context, "নোটিফিকেশন পারমিশন মেলেনি", Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "সেটিংস",
                subtitle = "অ্যাপ কনফিগারেশন, লেখার সেটিংস ও রিমাইন্ডার",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ================= 1. ✍️ AUTHOR NAME & SIGNATURE SETTING =================
            var inputAuthorName by remember(currentAuthorSignatureName) { mutableStateOf(currentAuthorSignatureName) }
            ElevatedGlassCard {
                SectionHeader(
                    title = "লেখকের নাম ও স্বাক্ষর",
                    subtitle = "কবিতা ও PDF এক্সপোর্টের নিচে প্রদর্শিত লেখক স্বাক্ষর কনফিগার করুন",
                    icon = Icons.Default.Person,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputAuthorName,
                    onValueChange = { inputAuthorName = it },
                    label = { Text("লেখকের নাম / ছদ্মনাম") },
                    placeholder = { Text("উদা: কাজী নজরুল ইসলাম (মুছে ফেললে লুকানো থাকবে)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedLabelColor = GoldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Live Preview Card
                val previewSignature = PdfExportHelper.formatAuthorSignature(inputAuthorName)
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = if (previewSignature.isNotBlank()) "প্রিভিউ: $previewSignature" else "ℹ️ লেখক স্বাক্ষর সম্পূর্ণ লুকানো থাকবে",
                            fontSize = 12.5.sp,
                            fontWeight = if (previewSignature.isNotBlank()) FontWeight.Medium else FontWeight.Normal,
                            fontStyle = if (previewSignature.isNotBlank()) FontStyle.Italic else FontStyle.Normal,
                            color = if (previewSignature.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryActionButton(
                        text = "লুকান",
                        onClick = {
                            inputAuthorName = ""
                            viewModel.updateAuthorSignatureName("")
                            Toast.makeText(context, "লেখক স্বাক্ষর লুকানো হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    SecondaryActionButton(
                        text = "ডিফল্ট",
                        onClick = {
                            viewModel.resetAuthorSignatureName()
                            Toast.makeText(context, "ডিফল্ট নাম সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    PrimaryActionButton(
                        text = "সংরক্ষণ",
                        onClick = {
                            viewModel.updateAuthorSignatureName(inputAuthorName)
                            Toast.makeText(context, "লেখকের নাম সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // ================= 2. 📝 EDITOR TOP BAR NAME CUSTOMIZATION =================
            var inputTopBarName by remember(currentTopBarName) { mutableStateOf(currentTopBarName) }
            ElevatedGlassCard {
                SectionHeader(
                    title = "এডিটর ক্যানভাস শিরোনাম",
                    subtitle = "এডিটর স্ক্রিনের শীর্ষে আপনার নিজস্ব খাতার শিরোনাম প্রদর্শন করুন",
                    icon = Icons.Default.Edit,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputTopBarName,
                    onValueChange = { inputTopBarName = it },
                    label = { Text("টপ বার শিরোনাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedLabelColor = GoldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryActionButton(
                        text = "ডিফল্ট",
                        onClick = {
                            viewModel.resetEditorTopBarName()
                            Toast.makeText(context, "ডিফল্ট শিরোনাম সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    PrimaryActionButton(
                        text = "সংরক্ষণ",
                        onClick = {
                            viewModel.updateEditorTopBarName(inputTopBarName)
                            Toast.makeText(context, "টপ বার শিরোনাম সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // ================= 2. ✍️ WRITING SETTINGS =================
            ElevatedGlassCard {
                SectionHeader(
                    title = "ডিফল্ট লেখার সেটিংস",
                    subtitle = "নতুন তৈরি ও আগের বিদ্যমান সকল নোটের জন্য প্রযোজ্য ফন্ট, সাইজ ও অ্যালাইনমেন্ট",
                    icon = Icons.Default.Create,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // --- A. Default Font Size ---
                Text(
                    text = "ডিফল্ট ফন্ট সাইজ (Font Size)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                SegmentedPillSelector(
                    items = listOf(
                        "small" to "ছোট (১৪sp)",
                        "medium" to "মাঝারি (১৮sp)",
                        "large" to "বড় (২২sp)"
                    ),
                    selectedItem = defaultFontSizeKey,
                    onItemSelected = { key ->
                        viewModel.setDefaultFontSizeKey(key, applyToAllExisting = true)
                        Toast.makeText(context, "ডিফল্ট ফন্ট সাইজ সকল নোটে প্রয়োগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.dp))

                // --- B. Default Font Style Selector ---
                val selectedFont = BengaliFonts.getFontByKey(defaultFontFamilyKey)
                Text(
                    text = "ডিফল্ট ফন্ট স্টাইল (Font Style)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            GoldPrimary.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { showFontPickerSheet = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
                            Text(
                                text = selectedFont.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "প্রিভিউ: শব্দের ক্যানভাসে স্বপ্নের আল্পনা",
                                fontFamily = selectedFont.fontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FontDownload,
                                contentDescription = "Select Font",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.dp))

                // --- C. Default Text Alignment ---
                Text(
                    text = "ডিফল্ট টেক্সট অ্যালাইনমেন্ট (Text Alignment)",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                SegmentedPillSelector(
                    items = listOf(
                        "LEFT" to "বামে",
                        "CENTER" to "মাঝে",
                        "RIGHT" to "ডানে"
                    ),
                    selectedItem = defaultTextAlignKey,
                    onItemSelected = { alignKey ->
                        viewModel.setDefaultTextAlignKey(alignKey, applyToAllExisting = true)
                        val label = when (alignKey) {
                            "LEFT" -> "বামে"
                            "CENTER" -> "মাঝে"
                            else -> "ডানে"
                        }
                        Toast.makeText(context, "ডিফল্ট অ্যালাইনমেন্ট '$label' সকল নোটে নির্ধারণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    iconMapper = { key ->
                        val icon = when (key) {
                            "LEFT" -> Icons.Default.FormatAlignLeft
                            "CENTER" -> Icons.Default.FormatAlignCenter
                            else -> Icons.Default.FormatAlignRight
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "💡 এই সেটিংস নতুন ও আগের সকল নোটে স্বয়ংক্রিয়ভাবে কাজ করে।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PrimaryActionButton(
                            text = "সকল নোটে প্রয়োগ",
                            onClick = {
                                viewModel.applyDefaultWritingStylesToAllNotes()
                                Toast.makeText(context, "বিদ্যমান সকল নোটে ডিফল্ট স্টাইল প্রয়োগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // ================= 3. 🔔 NOTIFICATION & REMINDER =================
            ElevatedGlassCard {
                SectionHeader(
                    title = "নোটিফিকেশন ও রিমাইন্ডার",
                    subtitle = "দৈনিক লেখার অনুপ্রেরণাদায়ক বার্তা ও রিমাইন্ডার শিডিউল",
                    icon = Icons.Default.NotificationsActive,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingItemRow(
                    title = "রিমাইন্ডার সিস্টেম",
                    subtitle = if (isReminderMasterEnabled) "রিমাইন্ডার মাস্টার অপশন চালু আছে" else "রিমাইন্ডার সম্পূর্ণ বন্ধ আছে",
                    icon = Icons.Default.NotificationsActive,
                    iconTint = GoldPrimary,
                    badgeBgTint = GoldPrimary.copy(alpha = 0.15f)
                ) {
                    CustomThemeSwitch(
                        checked = isReminderMasterEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        showPermissionDeniedBanner = false
                                        viewModel.setReminderMasterEnabled(context, true)
                                        Toast.makeText(context, "রিমাইন্ডার চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    } else {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.setReminderMasterEnabled(context, true)
                                    Toast.makeText(context, "রিমাইন্ডার চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                viewModel.setReminderMasterEnabled(context, false)
                                Toast.makeText(context, "রিমাইন্ডার বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                // Permission Denied Banner (Android 13+)
                if (showPermissionDeniedBanner) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "নোটিফিকেশন পারমিশন লাগবে, ফোনের সেটিংস থেকে অন করুন",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ফোনের সেটিংসে যান", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (isReminderMasterEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingItemRow(
                        title = "দৈনিক লেখার Reminder",
                        subtitle = "প্রতিদিন নির্ধারিত সময়ে প্রেরণা জাগানিয়া নোটিফিকেশন পাঠাবে",
                        icon = Icons.Default.AccessTime,
                        iconTint = GoldPrimary,
                        badgeBgTint = GoldPrimary.copy(alpha = 0.15f)
                    ) {
                        CustomThemeSwitch(
                            checked = isDailyReminderEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.setDailyReminderEnabled(context, enabled)
                            }
                        )
                    }

                    if (isDailyReminderEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Time Picker Selection Card
                        val formattedTime = formatBengaliTime(reminderHour, reminderMinute)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.dp,
                                    GoldPrimary.copy(alpha = 0.35f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    val timePicker = android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            viewModel.setReminderTime(context, hourOfDay, minute)
                                            Toast.makeText(context, "রিমাইন্ডারের সময় আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        reminderHour,
                                        reminderMinute,
                                        false
                                    )
                                    timePicker.show()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(GoldPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "Time Picker",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "রিমাইন্ডারের সময়",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formattedTime,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    }
                                }

                                SecondaryActionButton(
                                    text = "সময় পরিবর্তন",
                                    onClick = {
                                        val timePicker = android.app.TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                viewModel.setReminderTime(context, hourOfDay, minute)
                                                Toast.makeText(context, "রিমাইন্ডারের সময় আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                            },
                                            reminderHour,
                                            reminderMinute,
                                            false
                                        )
                                        timePicker.show()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Smart Reminder Info Tip Callout Box
                        CalloutInfoBox(
                            text = "স্মার্ট রিমাইন্ডার: যদি আপনি সেদিন ইতিমধ্যে অন্তত ১টি নতুন বা সংশোধিত নোট লিখে থাকেন, সেদিনের রিমাইন্ডার স্বয়ংক্রিয়ভাবে স্কিপ হবে।"
                        )
                    }
                }
            }

            // ================= 4. GLOBAL FONT SIZE PREFERENCE =================
            ElevatedGlassCard {
                SectionHeader(
                    title = "গ্লোবাল অ্যাপ ফন্ট ডিসপ্লে সাইজ",
                    subtitle = "সম্পূর্ণ অ্যাপ্লিকেশনের টেক্সট ডিসপ্লে সাইজ নির্বাচন করুন",
                    icon = Icons.Default.FormatSize,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SegmentedPillSelector(
                    items = listOf(
                        "small" to "ছোট",
                        "medium" to "মাঝারি",
                        "large" to "বড়"
                    ),
                    selectedItem = fontSizePreference,
                    onItemSelected = { key ->
                        viewModel.setFontSizePreference(key)
                        val label = when (key) {
                            "small" -> "ছোট"
                            "medium" -> "মাঝারি"
                            else -> "বড়"
                        }
                        Toast.makeText(context, "ডিসপ্লে সাইজ '$label' নির্ধারণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ================= 5. SECURITY PASSWORD =================
            ElevatedGlassCard {
                SectionHeader(
                    title = "সিকিউরিটি ও গোপনীয়তা",
                    subtitle = "হাইডেন নোটস খোলার জন্য সিকিউরিটি পিন বা পাসওয়ার্ড সেটিং",
                    icon = Icons.Default.Security,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingItemRow(
                    title = "পাসওয়ার্ড লক",
                    subtitle = if (passwordHash.isNullOrEmpty()) "পাসওয়ার্ড সেট করা হয়নি" else "পাসওয়ার্ড সক্রিয় রয়েছে",
                    icon = Icons.Default.Security,
                    iconTint = if (passwordHash.isNullOrEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else GoldPrimary,
                    badgeBgTint = if (passwordHash.isNullOrEmpty()) MaterialTheme.colorScheme.surfaceVariant else GoldPrimary.copy(alpha = 0.15f)
                ) {
                    PrimaryActionButton(
                        text = if (passwordHash.isNullOrEmpty()) "সেট করুন" else "পরিবর্তন",
                        onClick = { showSetPasswordDialog = true }
                    )
                }
            }

            // ================= 6. DARK MODE TOGGLE =================
            val effectiveDark = isDarkMode ?: true
            ElevatedGlassCard {
                SectionHeader(
                    title = "অ্যাপ Appearance (থিম)",
                    subtitle = "গাঢ় (Dark) ও হালকা (Light) থিমের মধ্যে পরিবর্তন করুন",
                    icon = Icons.Default.DarkMode,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingItemRow(
                    title = "ডার্ক মোড (Dark Mode)",
                    subtitle = if (effectiveDark) "গাঢ় থিম সক্রিয় রয়েছে" else "হালকা থিম সক্রিয় রয়েছে",
                    icon = Icons.Default.DarkMode,
                    iconTint = GoldPrimary,
                    badgeBgTint = GoldPrimary.copy(alpha = 0.15f)
                ) {
                    CustomThemeSwitch(
                        checked = effectiveDark,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }

            // ================= 7. DANGER ZONE (CLEAR CACHE) =================
            ElevatedGlassCard(
                borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
            ) {
                SectionHeader(
                    title = "বিপজ্জনক এলাকা (Danger Zone)",
                    subtitle = "অ্যাপের সাময়িক ফাইল ও ক্যাশ ডেটা মুছে ফেলার এলাকা",
                    icon = Icons.Default.Warning,
                    iconTint = MaterialTheme.colorScheme.error,
                    badgeBgTint = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingItemRow(
                    title = "ক্যাশ ক্লিয়ার করুন",
                    subtitle = "নোট বা মূল তথ্য নিরাপদ রেখে সাময়িক ক্যাশ মুছবে",
                    icon = Icons.Default.CleaningServices,
                    iconTint = MaterialTheme.colorScheme.error,
                    badgeBgTint = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                ) {
                    DestructiveActionButton(
                        text = "ক্যাশ ক্লিয়ার",
                        onClick = { showClearCacheDialog = true }
                    )
                }
            }
        }
    }

    if (showFontPickerSheet) {
        FontPickerSheet(
            sheetState = fontSheetState,
            selectedFontKey = defaultFontFamilyKey,
            onFontSelected = { fontKey ->
                viewModel.setDefaultFontFamilyKey(fontKey, applyToAllExisting = true)
                Toast.makeText(context, "ডিফল্ট ফন্ট সকল নোটে আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                showFontPickerSheet = false
            },
            onDismiss = { showFontPickerSheet = false }
        )
    }

    if (showSetPasswordDialog) {
        SetPasswordDialog(
            onDismiss = { showSetPasswordDialog = false },
            onSetPassword = { newPass, q, a ->
                viewModel.setAppPassword(newPass, q, a)
                Toast.makeText(context, "পাসওয়ার্ড সেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                showSetPasswordDialog = false
            }
        )
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "ক্যাশ ক্লিয়ার করার নিশ্চিতকরণ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "অ্যাপের সাময়িক ফাইল ও ক্যাশ মুছে ফেলা হবে। আপনার লিখিত কোনো কবিতা, নোট বা মূল তথ্য মুছে যাবে না। আপনি কি নিশ্চিত?",
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            context.cacheDir.deleteRecursively()
                            Toast.makeText(context, "ক্যাশ সফলভাবে ক্লিয়ার করা হয়েছে", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "ক্যাশ ক্লিয়ারে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                        showClearCacheDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ক্যাশ ক্লিয়ার করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("বাতিল", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ================= REUSABLE CUSTOM DESIGN SYSTEM COMPONENTS =================

@Composable
private fun ElevatedGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GoldPrimary.copy(alpha = 0.35f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.15f),
                        borderColor.copy(alpha = 0.45f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = GoldPrimary,
    badgeBgTint: Color = GoldPrimary.copy(alpha = 0.15f)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(badgeBgTint)
                .border(1.dp, iconTint.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppTitleFont,
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SettingItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = GoldPrimary,
    badgeBgTint: Color = GoldPrimary.copy(alpha = 0.12f),
    controlContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(badgeBgTint)
                    .border(1.dp, iconTint.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        controlContent()
    }
}

@Composable
private fun CustomThemeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) GoldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 250),
        label = "trackColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = tween(durationMillis = 250),
        label = "thumbOffset"
    )

    Box(
        modifier = modifier
            .width(50.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (checked) Brush.horizontalGradient(listOf(GoldLight, GoldPrimary))
                else Brush.horizontalGradient(listOf(trackColor, trackColor))
            )
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun <T> SegmentedPillSelector(
    items: List<Pair<T, String>>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    iconMapper: (@Composable (T) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                RoundedCornerShape(50)
            )
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (key, label) ->
                val isSelected = key == selectedItem
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(durationMillis = 200),
                    label = "segmentText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(GoldLight, GoldPrimary, GoldDark))
                            else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onItemSelected(key) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (iconMapper != null) {
                            iconMapper(key)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(listOf(GoldLight, GoldPrimary, GoldDark)))
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.2.dp, GoldPrimary, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = GoldPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DestructiveActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CalloutInfoBox(
    text: String,
    icon: ImageVector = Icons.Default.Lightbulb,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GoldPrimary.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = GoldPrimary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun formatBengaliTime(hour: Int, minute: Int): String {
    val periodStr = when {
        hour in 4..11 -> "সকাল"
        hour in 12..14 -> "দুপুর"
        hour in 15..17 -> "বিকেল"
        hour in 18..19 -> "সন্ধ্যা"
        else -> "রাত"
    }
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    fun toBengaliDigits(num: Int): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return num.toString().map { if (it.isDigit()) bnDigits[it.digitToInt()] else it }.joinToString("")
    }
    val hourBn = toBengaliDigits(displayHour)
    val minuteBn = toBengaliDigits(minute).padStart(2, '০')
    return "$periodStr $hourBn:$minuteBn মিনিট"
}
