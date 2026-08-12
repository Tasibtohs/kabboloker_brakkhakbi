package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.R
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppBodyFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppTitleFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary

data class VersionHistoryItem(
    val version: String,
    val releaseDate: String,
    val isLatest: Boolean = false,
    val highlights: List<String>
)

private val appUpdateHistory = listOf(
    VersionHistoryItem(
        version = "v১.০.০",
        releaseDate = "১২ আগস্ট, ২০২৬",
        isLatest = true,
        highlights = listOf(
            "✍️ ডিফল্ট লেখার সেটিংস (ফন্ট সাইজ, ফন্ট স্টাইল ও অ্যালাইনমেন্ট)।",
            "🔔 স্মার্ট দৈনিক লেখার রিমাইন্ডার ও টাইম পিকার নোটিফিকেশন।",
            "🎨 ২০টি জনপ্রিয় নান্দনিক বাংলা ফন্ট সাপোর্ট ও কাস্টম প্রিভিউ।",
            "🔒 হাইডেন নোটসের জন্য সিকিউরিটি পাসওয়ার্ড এবং রিকভারি প্রশ্ন।",
            "📄 অফলাইন Room ডাটাবেস, ব্যাকআপ ইমপোর্ট/এক্সপোর্ট ও PDF জেনারেটর।"
        )
    ),
    VersionHistoryItem(
        version = "v০.৯.০ (বেটা)",
        releaseDate = "১৫ জুলাই, ২০২৬",
        isLatest = false,
        highlights = listOf(
            "কাব্যপ্রেমীদের জন্য অফলাইন টেক্সট এডিটর ক্যানভাস রোলআউট।",
            "গ্রুপ ফোল্ডার ও পিন নোট ব্যবস্থাপনা।",
            "ডার্ক থিম এবং রিসাইকেল বিন (ট্র্যাশ) ফিচার।"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "অ্যাপ সম্পর্কে",
                subtitle = "লেখক, কবি ও অ্যাপ পরিচিতি",
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

            // ================= 1. DEVELOPER & AUTHOR PROFILE STAMP =================
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "তৈরি করেছেন",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                contentDescription = "এইচ.এম. ইব্রাহীম ত্বহা সরকার",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

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

                        Text(
                            text = "“কাব্যলোকের ব্রহ্মকবি”",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = AppTitleFont,
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                                ContactCardItem(
                                    modifier = Modifier.weight(1f),
                                    title = "ফেসবুক",
                                    subtitle = "প্রোফাইল খুলুন",
                                    badgeText = "FB",
                                    badgeColor = Color(0xFF1877F2),
                                    onClick = {
                                        openUrl(context, "https://www.facebook.com/h.m.ibrahimtohasarkar")
                                    }
                                )

                                ContactCardItem(
                                    modifier = Modifier.weight(1f),
                                    title = "ইনস্টাগ্রাম",
                                    subtitle = "প্রোফাইল খুলুন",
                                    badgeText = "IG",
                                    badgeColor = Color(0xFFE4405F),
                                    onClick = {
                                        openUrl(context, "https://www.instagram.com/h.m.ibrahimtohasarkar")
                                    }
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ContactCardItem(
                                    modifier = Modifier.weight(1f),
                                    title = "হোয়াটসঅ্যাপ",
                                    subtitle = "+8801308556665",
                                    badgeText = "WA",
                                    badgeColor = Color(0xFF25D366),
                                    onClick = {
                                        openWhatsApp(context, "8801308556665")
                                    }
                                )

                                ContactCardItem(
                                    modifier = Modifier.weight(1f),
                                    title = "ইমেইল",
                                    subtitle = "hmibrahimsarkar712@gmail.com",
                                    badgeText = "✉",
                                    icon = Icons.Default.Email,
                                    badgeColor = GoldPrimary,
                                    onClick = {
                                        openEmail(context, "hmibrahimsarkar712@gmail.com", "কাব্যলোকের ব্রহ্মকবি - যোগাযোগ")
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
                        BadgeChipItem(text = "ভার্সন ১.০.০")
                        BadgeChipItem(text = "১০০% অফলাইন")
                        BadgeChipItem(text = "Room ডাটাবেস")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalWifiOff,
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

            // ================= 3. DEVELOPER NOTE SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, GoldPrimary.copy(alpha = 0.45f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote",
                        tint = GoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📜 ডেভেলপারের কাব্যিক ভাবনা",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "“শব্দের প্রতি ভালোবাসা থেকেই এই ছোট্ট প্রয়াস—\nকিছু অনুভূতি, কিছু কবিতা আর কিছু না বলা কথা\nযেন হারিয়ে না যায়।”",
                        fontSize = 14.5.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = AppTitleFont,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "— এইচ.এম. ইব্রাহীম ত্বহা সরকার (কাব্যলোকের ব্রহ্মকবি)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ================= 4. VERSION & UPDATE INFO SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
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
                            imageVector = Icons.Default.NewReleases,
                            contentDescription = "Version Info",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "📋 ভার্সন ও আপডেট ইনফো (Version & Update)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Version Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "বর্তমান ভার্সন: v১.০.০",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "রিলিজ তারিখ: ১২ আগস্ট, ২০২৬",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = "সর্বশেষ রিলিজ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // What's New List
                    Text(
                        text = "✨ নতুন কী আছে (What's New):",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    appUpdateHistory.firstOrNull()?.highlights?.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Text(
                                text = feature,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Update History Timeline (Expandable)
                    var isHistoryExpanded by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isHistoryExpanded = !isHistoryExpanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আপডেট ইতিহাস (Update History)",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (isHistoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle History",
                            tint = GoldPrimary
                        )
                    }

                    AnimatedVisibility(
                        visible = isHistoryExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            appUpdateHistory.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.version,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = item.releaseDate,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    item.highlights.forEach { hl ->
                                        Text(
                                            text = "— $hl",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= 5. FEEDBACK & SUPPORT SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
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
                            imageVector = Icons.Default.Feedback,
                            contentDescription = "Feedback & Support",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "💬 ফিডব্যাক ও সাপোর্ট (Feedback & Support)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FeedbackActionButton(
                                modifier = Modifier.weight(1f),
                                title = "💬 মতামত পাঠান",
                                subtitle = "আপনার ভালো লাগা জানান",
                                icon = Icons.Default.Feedback,
                                iconColor = GoldPrimary,
                                onClick = {
                                    openEmail(
                                        context,
                                        "hmibrahimsarkar712@gmail.com",
                                        "কাব্যলোকের ব্রহ্মকবি - মতামত / ফিডব্যাক"
                                    )
                                }
                            )

                            FeedbackActionButton(
                                modifier = Modifier.weight(1f),
                                title = "🐞 Bug Report",
                                subtitle = "সমস্যার বিবরণ লিখুন",
                                icon = Icons.Default.BugReport,
                                iconColor = MaterialTheme.colorScheme.error,
                                onClick = {
                                    openEmail(
                                        context,
                                        "hmibrahimsarkar712@gmail.com",
                                        "কাব্যলোকের ব্রহ্মকবি - বাগ রিপোর্ট"
                                    )
                                }
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FeedbackActionButton(
                                modifier = Modifier.weight(1f),
                                title = "💡 Feature Request",
                                subtitle = "নতুন আইডিয়া প্রস্তাব",
                                icon = Icons.Default.Lightbulb,
                                iconColor = GoldLight,
                                onClick = {
                                    openEmail(
                                        context,
                                        "hmibrahimsarkar712@gmail.com",
                                        "কাব্যলোকের ব্রহ্মকবি - নতুন ফিচারের অনুরোধ"
                                    )
                                }
                            )

                            FeedbackActionButton(
                                modifier = Modifier.weight(1f),
                                title = "📩 Contact Dev",
                                subtitle = "সরাসরি সাপোর্ট টিম",
                                icon = Icons.Default.Person,
                                iconColor = Color(0xFF25D366),
                                onClick = {
                                    openWhatsApp(context, "8801308556665")
                                }
                            )
                        }
                    }
                }
            }

            // ================= 6. CREDITS SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
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
                            imageVector = Icons.Default.Code,
                            contentDescription = "Credits",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "🧩 কৃতজ্ঞতা স্বীকার (Credits)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CreditItemRow(
                            title = "ডিজাইন ও ডেভেলপমেন্ট",
                            subtitle = "এইচ.এম. ইব্রাহীম ত্বহা সরকার (কাব্যলোকের ব্রহ্মকবি)",
                            icon = Icons.Default.Palette
                        )

                        CreditItemRow(
                            title = "আইকন ও ভিজ্যুয়াল আর্ট",
                            subtitle = "Google Material Icons & Adaptive Vector Resources",
                            icon = Icons.Default.Layers
                        )

                        CreditItemRow(
                            title = "ফন্ট ও টাইপোগ্রাফি",
                            subtitle = "SolaimanLipi, Kalpurush, Noto Serif Bengali, Hind Siliguri, Ekushey Fonts",
                            icon = Icons.Default.FontDownload
                        )

                        CreditItemRow(
                            title = "লাইব্রেরি ও ফ্রেমওয়ার্ক",
                            subtitle = "Android Jetpack Compose, Kotlin Coroutines, Room Database, Material 3",
                            icon = Icons.Default.Code
                        )
                    }
                }
            }

            // ================= 7. SHARE APP BUTTON =================
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("বন্ধুদের সাথে শেয়ার করুন", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun FeedbackActionButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CreditItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.15f))
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ContactCardItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = badgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BadgeChipItem(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GoldPrimary.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = GoldPrimary)
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "লিঙ্ক খুলতে ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show()
    }
}

private fun openWhatsApp(context: Context, phoneWithCountryCode: String) {
    try {
        val url = "https://api.whatsapp.com/send?phone=$phoneWithCountryCode"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "হোয়াটসঅ্যাপ ইনস্টল করা নেই", Toast.LENGTH_SHORT).show()
    }
}

private fun openEmail(context: Context, emailAddress: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ইমেইল অ্যাপ খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}
