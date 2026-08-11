package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ripple
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.R
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.SetPasswordDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppBodyFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppTitleFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.SoftLavender
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAboutScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val passwordHash by viewModel.appPasswordHash.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsState()
    val fontSizePreference by viewModel.fontSizePreference.collectAsState()
    val currentTopBarName by viewModel.editorTopBarName.collectAsState()

    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "সেটিংস ও অ্যাপ সম্পর্কে",
                subtitle = "কাব্যলোকের ব্রহ্মকবি অ্যাপ বিবরণী",
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ================= 1. DEVELOPER CREDIT SECTION (GLASSMORPHISM STAMP CARD) =================
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "তৈরি করেছেন",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Glassmorphic Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(GoldLight, GoldPrimary, GoldDark.copy(alpha = 0.6f))
                            ),
                            shape = RoundedCornerShape(22.dp)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Author Profile Avatar Badge
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .shadow(8.dp, CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            GoldLight.copy(alpha = 0.6f),
                                            GoldPrimary.copy(alpha = 0.25f)
                                        )
                                    )
                                )
                                .border(2.5.dp, Brush.linearGradient(listOf(GoldLight, GoldPrimary, GoldDark)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.author_profile_photo_1785829533777),
                                contentDescription = "এইচ.এম. ইব্রাহীম ত্বহা সরকার (কাব্যলোকের ব্রহ্মকবি)",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Calligraphic Developer Name
                        val goldBrush = Brush.horizontalGradient(
                            colors = listOf(GoldLight, GoldPrimary, GoldDark)
                        )

                        Text(
                            text = "এইচ.এম. ইব্রাহীম ত্বহা সরকার",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppTitleFont,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(brush = goldBrush)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Golden Pen Name
                        Text(
                            text = "“কাব্যলোকের ব্রহ্মকবি”",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = AppTitleFont,
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Italic Motto / Line
                        Text(
                            text = "“শব্দে ও ছন্দে গাঁথা জীবনের অনাবিল অনুভূতিমালা”",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = AppBodyFont,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = GoldPrimary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(18.dp))

                        // Contact Options Section
                        Text(
                            text = "যোগাযোগের মাধ্যমসমূহ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ContactItemCard(
                                    modifier = Modifier.weight(1f),
                                    title = "ফেসবুক",
                                    subtitle = "প্রোফাইল খুলুন",
                                    badgeText = "FB",
                                    badgeColor = Color(0xFF1877F2),
                                    onClick = {
                                        openFacebook(context, "https://www.facebook.com/h.m.ibrahimtohasarkar")
                                    }
                                )

                                ContactItemCard(
                                    modifier = Modifier.weight(1f),
                                    title = "ইনস্টাগ্রাম",
                                    subtitle = "প্রোফাইল খুলুন",
                                    badgeText = "IG",
                                    badgeColor = Color(0xFFE4405F),
                                    onClick = {
                                        openInstagram(context, "https://www.instagram.com/h.m.ibrahimtohasarkar")
                                    }
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ContactItemCard(
                                    modifier = Modifier.weight(1f),
                                    title = "হোয়াটসঅ্যাপ",
                                    subtitle = "+8801308556665",
                                    badgeText = "WA",
                                    badgeColor = Color(0xFF25D366),
                                    onClick = {
                                        openWhatsApp(context, "8801308556665")
                                    }
                                )

                                ContactItemCard(
                                    modifier = Modifier.weight(1f),
                                    title = "ইমেইল",
                                    subtitle = "hmibrahimsarkar712@gmail.com",
                                    badgeText = "✉",
                                    icon = Icons.Default.Email,
                                    badgeColor = GoldPrimary,
                                    onClick = {
                                        openEmail(
                                            context,
                                            "hmibrahimsarkar712@gmail.com",
                                            "কাব্যলোকের ব্রহ্মকবি - যোগাযোগ"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ================= 2. ABOUT APP SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "অ্যাপ সম্পর্কে",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "‘কাব্যলোকের ব্রহ্মকবি’ হলো কবি, সাহিত্যিক ও বাংলা শব্দপ্রেমীদের জন্য তৈরি একটি বিশেষায়িত কবিতা ও নোট লেখার ডিজিটাল ক্যানভাস। আপনার প্রিয় কবিতা, গান, গজল কিংবা ব্যক্তিগত ভাবনাসমূহকে নান্দনিক ফন্ট, কালার ও সুরক্ষার সাথে সংরক্ষণ করার পূর্ণ স্বাধীনতা দেয় এই অ্যাপটি।",
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgeChip(text = "ভার্সন ১.০.০")
                        BadgeChip(text = "১০০% অফলাইন")
                        BadgeChip(text = "Room ডাটাবেস")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SignalWifiOff,
                            contentDescription = "Offline",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "১০০% অফলাইন ও সম্পূর্ণ নিরাপদ। আপনার কোনো ডাটা ইন্টারনেটে আপলোড হয় না।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ================= 3. SETTINGS & PREFERENCES =================
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "সেটিংস ও পছন্দসমূহ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Dark Mode Toggle Card
                val effectiveDark = isDarkMode ?: true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
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
                            Icon(
                                imageVector = Icons.Filled.DarkMode,
                                contentDescription = "Dark Mode",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ডার্ক মোড (Dark Mode)",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (effectiveDark) "গাঢ় মোড সক্রিয়" else "হালকা মোড সক্রিয়",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = effectiveDark,
                            onCheckedChange = { viewModel.toggleDarkMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldLight,
                                checkedTrackColor = GoldDark.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // Notification Setting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
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
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "নোটিফিকেশন ও রিমাইন্ডার",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isNotificationsEnabled) "দৈনিক লেখার নোটিফিকেশন চালু" else "নোটিফিকেশন বন্ধ রয়েছে",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isNotificationsEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.toggleNotifications(enabled)
                                Toast.makeText(
                                    context,
                                    if (enabled) "নোটিফিকেশন চালু করা হয়েছে" else "নোটিফিকেশন বন্ধ করা হয়েছে",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldLight,
                                checkedTrackColor = GoldDark.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // Font Size Preference Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.FormatSize,
                                contentDescription = "Font Size",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "গ্লোবাল ফন্ট সাইজ",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "কবিতা পড়া ও লেখার অক্ষরের আকার নির্বাচন করুন",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                "small" to "ছোট",
                                "medium" to "মাঝারি",
                                "large" to "বড়"
                            ).forEach { (key, label) ->
                                val isSelected = fontSizePreference == key
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setFontSizePreference(key)
                                        Toast.makeText(context, "ফন্ট সাইজ '$label' নির্ধারণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    label = { Text(text = label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Preview box
                        val sampleFontSize = when (fontSizePreference) {
                            "small" -> 13.sp
                            "large" -> 18.sp
                            else -> 15.sp
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "প্রিভিউ: “শব্দেই বাঁচে কবি, সুন্দরেই রচে কাব্য”",
                                fontSize = sampleFontSize,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Security Setting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
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
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = "Security",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "সিকিউরিটি পাসওয়ার্ড",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (passwordHash.isNullOrEmpty()) "পাসওয়ার্ড সেট করা হয়নি" else "পাসওয়ার্ড সক্রিয় রয়েছে",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showSetPasswordDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (passwordHash.isNullOrEmpty()) "সেট করুন" else "পরিবর্তন", color = GoldPrimary)
                        }
                    }
                }

                // Editor Top Bar Customization Card
                var inputTopBarName by remember(currentTopBarName) { mutableStateOf(currentTopBarName) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "টপ বারের নাম (এডিটর স্ক্রিন)",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "এডিটর স্ক্রিনের একদম উপরে নিজস্ব ছদ্মনাম বা খাতার নাম ব্যবহার করুন",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = inputTopBarName,
                            onValueChange = { inputTopBarName = it },
                            label = { Text("টপ বারের শিরোনাম") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.resetEditorTopBarName() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("ডিফল্ট", color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.updateEditorTopBarName(inputTopBarName)
                                    Toast.makeText(context, "টপ বার নাম সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                            ) {
                                Text("সংরক্ষণ", color = Color.White)
                            }
                        }
                    }
                }

                // App Actions: Clear Cache & Share App
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Clear Cache Button
                    OutlinedButton(
                        onClick = { showClearCacheDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Clear Cache",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ক্যাশ ক্লিয়ার", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }

                    // Share App Button
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "‘কাব্যলোকের ব্রহ্মকবি’ — আপনার মনের কবিতা, বাংলা গান ও সাহিত্য নোট লেখার সেরা অফলাইন অ্যাপ।"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "অ্যাপ শেয়ার করুন"))
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("অ্যাপ শেয়ার", color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            // ================= 4. SUPPORT & FAQ SECTION =================
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "সাপোর্ট ও সাহায্য",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Report Issue / Feedback Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Feedback,
                                    contentDescription = "Feedback",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "সাহায্য ও ফিডব্যাক পাঠান",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "আপনার যেকোনো বক্তব্য, পরামর্শ বা সমস্যা ইমেইলে সরাসরি ডেভেলপারকে জানান",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ScalePressButton(
                            onClick = {
                                openEmail(
                                    context = context,
                                    emailAddress = "hmibrahimsarkar712@gmail.com",
                                    subject = "অ্যাপ ফিডব্যাক",
                                    body = ""
                                )
                            },
                            backgroundColor = GoldPrimary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Send Feedback",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ফিডব্যাক পাঠান",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // FAQ Accordion Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "FAQ",
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "সাধারণ জিজ্ঞাসা (FAQ)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val faqs = remember {
                            listOf(
                                "অ্যাপে কি সিকিউরিটি পাসওয়ার্ড লক ব্যবহার করা যায়?" to
                                        "হ্যাঁ, 'সিকিউরিটি পাসওয়ার্ড' সেকশন থেকে পাসওয়ার্ড সেট করে আপনার গোপনীয় কবিতা ও নোট সুরক্ষিত রাখতে পারেন।",
                                "আমার লেখা কবিতাগুলো কোথায় সংরক্ষিত থাকে?" to
                                        "আপনার সমস্ত কবিতা ও নোট শতভাগ অফলাইনে আপনার ডিভাইসের অন-ডিভাইস Room ডাটাবেসে অত্যন্ত নিরাপদে সংরক্ষিত থাকে।",
                                "কীভাবে কবিতা ব্যাকআপ বা পিডিএফ (PDF) করব?" to
                                        "অ্যাপের ড্রয়ার মেনু থেকে 'সব নোট PDF করুন' বা 'ব্যাকআপ ও রিস্টোর' অপশন ব্যবহার করে আপনার কবিতা সংকলন ফাইল হিসেবে সংরক্ষণ করতে পারেন।",
                                "গ্লোবাল ফন্ট সাইজ কীভাবে কাজ করে?" to
                                        "ফন্ট সাইজ অপশন থেকে ছোট, মাঝারি বা বড় বেছে নিলে কবিতা পড়ার সময়ে লেখার আকার আপনার পছন্দ অনুযায়ী পরিবর্তিত হবে।"
                            )
                        }

                        faqs.forEachIndexed { index, (question, answer) ->
                            FaqAccordionItem(question = question, answer = answer)
                            if (index < faqs.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= COPYRIGHT FOOTER =================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "© ২০২৬ কাব্যলোকের ব্রহ্মকবি — সর্বস্বত্ব সংরক্ষিত",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "সংস্করণ ১.০.০ • ডিজাইন ও উদ্ভাবনে এইচ.এম. ইব্রাহীম ত্বহা সরকার",
                    fontSize = 11.sp,
                    color = GoldPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }

    // Passwords Dialog
    if (showSetPasswordDialog) {
        SetPasswordDialog(
            onSetPassword = { pass, q, a ->
                viewModel.setPassword(pass, q, a)
                showSetPasswordDialog = false
            },
            onDismiss = { showSetPasswordDialog = false }
        )
    }

    // Clear Cache Confirmation Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("ক্যাশ ক্লিয়ার করবেন?") },
            text = { Text("অ্যাপ ক্যাশ ক্লিয়ার করলে কোনো নোট বা কবিতা মুছে যাবে না, শুধুমাত্র সিস্টেমের সাময়িক ক্যাশ ফাইল মুছে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAppCache(context)
                        showClearCacheDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("হ্যাঁ, ক্লিয়ার করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("ফিডব্যাক ও পরামর্শ") },
            text = {
                Column {
                    Text("আপনার মূল্যবান বক্তব্য বা সমস্যার কথা লিখে ইমেইলে পাঠান:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("আপনার বার্তা লিখুন...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        openEmail(
                            context = context,
                            emailAddress = "hmibrahimsarkar712@gmail.com",
                            subject = "কাব্যলোকের ব্রহ্মকবি - ফিডব্যাক",
                            body = feedbackText
                        )
                        showFeedbackDialog = false
                        feedbackText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("পাঠান", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun ContactItemCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "contactScale"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = badgeText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ScalePressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = GoldPrimary,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        interactionSource = interactionSource,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        content()
    }
}

private fun openFacebook(context: android.content.Context, profileUrl: String) {
    try {
        val uri = Uri.parse("fb://facewebmodal/f?href=$profileUrl")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.facebook.katana")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileUrl))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "লিংকটি খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openInstagram(context: android.content.Context, profileUrl: String) {
    try {
        val uri = Uri.parse(profileUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.instagram.android")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileUrl))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "লিংকটি খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openWhatsApp(context: android.content.Context, phoneNumber: String) {
    val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
    val waUrl = "https://wa.me/$cleanNumber"
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
            setPackage("com.whatsapp")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "হোয়াটসঅ্যাপ খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openEmail(
    context: android.content.Context,
    emailAddress: String,
    subject: String = "",
    body: String = ""
) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
            if (subject.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, subject)
            if (body.isNotBlank()) putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ইমেইল অ্যাপ খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun FaqAccordionItem(
    question: String,
    answer: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Toggle",
                tint = GoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Text(
                text = answer,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun BadgeChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(GoldPrimary.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = GoldPrimary
        )
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ব্রাউজার বা অ্যাপটি খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
    }
}
