package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.VerifiedUser
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppTitleFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "প্রাইভেসি পলিসি",
                subtitle = "গোপনীয়তা ও ডেটা সুরক্ষা নীতি",
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
            // ================= 1. HEADER BANNER =================
            ElevatedGlassCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f))
                                .border(1.5.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = "Shield",
                                tint = GoldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "১০০% অফলাইন ও নিরাপদ",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppTitleFont,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "আপনার কোনো তথ্য ইন্টারনেটে আদান-প্রদান করা হয় না",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = GoldPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary.copy(alpha = 0.12f))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "সর্বশেষ হালনাগাদ: ১২ আগস্ট, ২০২৬",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = GoldPrimary
                            )
                        }

                        Text(
                            text = "v১.০.০ পলিসি",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ================= 2. QUICK SUMMARY CARD =================
            ElevatedGlassCard(
                borderColor = GoldPrimary.copy(alpha = 0.45f)
            ) {
                SectionHeader(
                    title = "সংক্ষেপে গোপনীয়তা নীতি",
                    subtitle = "Quick Summary",
                    icon = Icons.Outlined.FactCheck,
                    iconTint = GoldPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SummaryBulletItem(
                        text = "১০০% লোকাল ডেটা স্টোরেজ: আপনার তৈরি সকল কবিতা ও নোট কেবল আপনার নিজস্ব ফোনেই জমা থাকে।"
                    )
                    SummaryBulletItem(
                        text = "কোনো ক্লাউড বা অনলাইন ট্র্যাকিং নেই: অ্যাপটিতে কোনো সার্ভার বা থার্ড-পার্টি অ্যানালিটিক্স নেই।"
                    )
                    SummaryBulletItem(
                        text = "সিকিউরিটি ও এক্সপোর্ট স্বাধিকার: পাসওয়ার্ড দিয়ে সেফ রুম আনলক ও নিজের ডিভাইসে অফলাইন ব্যাকআপ।"
                    )
                    SummaryBulletItem(
                        text = "সম্পূর্ণ ডাটা নিয়ন্ত্রণ: যেকোনো সময় সব তথ্য মুছে ফেলা বা আনইনস্টল করে স্থায়ী মুছে ফেলার অধিকার।"
                    )
                }
            }

            // ================= 3. NUMBERED POLICY SECTIONS =================

            // Clause 1
            PolicySectionCard(
                numberBengali = "১",
                titleBengali = "তথ্যের গোপনীয়তা ও লোকাল স্টোরেজ",
                icon = Icons.Outlined.Storage,
                content = buildAnnotatedString {
                    append("‘কাব্যলোকের ব্রহ্মকবি’ অ্যাপে আপনার লিখিত সকল কবিতা, গান, গজল, সাহিত্যকর্ম বা ব্যক্তিগত নোট শুধুমাত্র আপনার নিজস্ব অ্যান্ড্রয়েড ডিভাইসের অভ্যন্তরীণ প্রাইভেট ডাটাবেসে (")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("Room Database")
                    }
                    append(") সুরক্ষিত ও বিচ্ছিন্ন অবস্থায় থাকে।\n\n")
                    append("উদাহরণস্বরূপ, এই ডাটাবেসটি অ্যাপের নিজস্ব সংরক্ষিত ফোল্ডারে (")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("/data/data/com.hmibrahimsarkar.kabboloker_brakkhakbi/")
                    }
                    append(") সংরক্ষিত থাকায় অন্য কোনো ইন্সটলকৃত অ্যাপ বা বাইরের সিস্টেম এটি অ্যাক্সেস করতে পারে না।")
                }
            )

            // Clause 2
            PolicySectionCard(
                numberBengali = "২",
                titleBengali = "ক্লাউড বা সার্ভার সংযোগের অনুপস্থিতি",
                icon = Icons.Outlined.CloudOff,
                content = buildAnnotatedString {
                    append("এই অ্যাপটি ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("সম্পূর্ণ অফলাইন-ভিত্তিক")
                    }
                    append("। এখানে কোনো দূরবর্তী (Remote) ক্লাউড সার্ভার, ব্যাকএন্ড এপিআই বা অটো-সিঙ্ক সার্ভিস ব্যবহার করা হয়নি।\n\n")
                    append("অ্যাপটি ব্যবহার করার জন্য ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("কোনো প্রকার ইন্টারনেট সংযোগের প্রয়োজন হয় না")
                    }
                    append("। ফলে অনাকাঙ্ক্ষিত তথ্য ফাঁস, সার্ভার হ্যাকিং বা ক্লাউড লিকের কোনো সুযোগই বিদ্যমান নেই।")
                }
            )

            // Clause 3
            PolicySectionCard(
                numberBengali = "৩",
                titleBengali = "কোনো থার্ড-পার্টি শেয়ারিং বা ট্র্যাকিং নেই",
                icon = Icons.Outlined.VerifiedUser,
                content = buildAnnotatedString {
                    append("যেহেতু অ্যাপটি কোনো ইন্টারনেটে যুক্ত নয়, তাই আপনার লেখা বিষয়বস্তু বা ব্যক্তিগত তথ্য ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("কোনো তৃতীয় পক্ষের (Third-party) কাছে পাঠানো বা ট্র্যাকিং করা সম্পূর্ণ অসম্ভব")
                    }
                    append("।\n\n")
                    append("অ্যাপটিতে কোনো প্রকার ইউজারেজ অ্যানালিটিক্স (যেমন Firebase Analytics), বিজ্ঞাপন নেটওয়ার্ক (AdMob) বা ব্যাকগ্রাউন্ড ট্র্যাকিং এসডিকে অন্তর্ভুক্ত করা হয়নি।")
                }
            )

            // Clause 4
            PolicySectionCard(
                numberBengali = "৪",
                titleBengali = "সিকিউরিটি ও পাসওয়ার্ড সুরক্ষা",
                icon = Icons.Outlined.Key,
                content = buildAnnotatedString {
                    append("অ্যাপের ‘হাইডেন নোটস’ ফিচারের নিরাপত্তার জন্য আপনি যে পাসওয়ার্ড সেট করবেন, তা ক্রিপ্টোগ্রাফিক ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("SHA-256 হ্যাশিং (একমুখী এনক্রিপশন)")
                    }
                    append(" প্রক্রিয়ার মাধ্যমে স্থানীয়ভাবে ডিভাইসে সংরক্ষিত থাকে।\n\n")
                    append("এর ফলে মূল পাসওয়ার্ডটি প্লেইন টেক্সট হিসেবে ডাটাবেসে সেভ থাকে না, এবং ডেভেলপার বা তৃতীয় কারো পক্ষেই আসল পাসওয়ার্ড উদ্ধার করা সম্ভব নয়।")
                }
            )

            // Clause 5
            PolicySectionCard(
                numberBengali = "৫",
                titleBengali = "ডাটা ব্যাকআপ ও এক্সপোর্ট নিয়ন্ত্রণ",
                icon = Icons.Outlined.FolderZip,
                content = buildAnnotatedString {
                    append("নোটের ব্যাকআপ (")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(".json")
                    }
                    append(") তৈরি বা ডকুমেন্ট এক্সপোর্ট (")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(".pdf")
                    }
                    append(") করার প্রক্রিয়াটি ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("১০০% লোকালি আপনার ফোনেই সম্পাদিত হয়")
                    }
                    append("।\n\n")
                    append("তৈরি হওয়া ফাইলগুলি কেবল আপনার ডিভাইসের পছন্দের স্থান বা ডাউনলোড ফোল্ডারে সেভ হয় এবং আপনি নিজেই তা সম্পূর্ণ নিয়ন্ত্রণ করেন।")
                }
            )

            // Clause 6
            PolicySectionCard(
                numberBengali = "৬",
                titleBengali = "অ্যাপের অনুমতিসমূহ (Permissions) ব্যাখ্যা",
                icon = Icons.Outlined.AdminPanelSettings,
                content = buildAnnotatedString {
                    append("অ্যাপের সুনির্দিষ্ট ফিচার পরিচালনার জন্য নিম্নলিখিত সিস্টেম পারমিশন প্রয়োজন হতে পারে:\n\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("• নোটিফিকেশন পারমিশন (POST_NOTIFICATIONS): ")
                    }
                    append("প্রতিদিন নির্ধারিত সময়ে লেখার অনুপ্রেরণাদায়ক রিমাইন্ডার প্রেরণের জন্য (অ্যান্ড্রয়েড ১৩+)।\n\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("• ফাইল ও স্টোরেজ এক্সেস: ")
                    }
                    append("আপনার পছন্দের ফোল্ডারে ব্যাকআপ ফাইল সংরক্ষণ ও এক্সপোর্টেড পিডিএফ সেভ করার জন্য।\n\n")
                    append("কোনো পারমিশনই কোনো গোপন ট্র্যাকিং বা অনাকাঙ্ক্ষিত ব্যাকগ্রাউন্ড কাজের জন্য ব্যবহার করা হয় না।")
                }
            )

            // Clause 7
            PolicySectionCard(
                numberBengali = "৭",
                titleBengali = "ইউজারের স্বাধিকার ও তথ্য মুছে ফেলার অধিকার",
                icon = Icons.Outlined.AssignmentTurnedIn,
                content = buildAnnotatedString {
                    append("আপনার নিজস্ব সৃষ্টিকর্ম ও তথ্যের ওপর ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("সম্পূর্ণ সার্বভৌম নিয়ন্ত্রণ আপনার")
                    }
                    append("।\n\n")
                    append("আপনি যেকোনো সময় যেকোনো নোট সম্পাদনা, ব্যাকআপ গ্রহণ, ট্র্যাশ বিন খালি বা স্থায়ীভাবে মুছে ফেলতে পারেন। এছাড়া অ্যাপটি ডিভাইস থেকে আনইনস্টল করার সাথে সাথে ডাটাবেসে থাকা সব তথ্য চিরতরে মুছে যায়।")
                }
            )

            // Clause 8
            PolicySectionCard(
                numberBengali = "৮",
                titleBengali = "শিশুদের গোপনীয়তা (Children's Privacy)",
                icon = Icons.Outlined.ChildCare,
                content = buildAnnotatedString {
                    append("‘কাব্যলোকের ব্রহ্মকবি’ অ্যাপটি শিশুসহ সকল বয়সের মানুষের জন্য সম্পূর্ণ নিরাপদ। অ্যাপটিতে কোনো ব্যক্তিগত তথ্য সংগ্রহ বা আদান-প্রদানের সুযোগ না থাকায় এটি শিশু বা কিশোরদের গোপনীয়তা লঙ্ঘনের কোনো ঝুঁকি তৈরি করে না।")
                }
            )

            // Clause 9
            PolicySectionCard(
                numberBengali = "৯",
                titleBengali = "পলিসি পরিবর্তনের অধিকার",
                icon = Icons.Outlined.Update,
                content = buildAnnotatedString {
                    append("ডেভেলপার ভবিষ্যতে অ্যাপের নতুন ফিচার সংযোজনের সংগতি রেখে এই গোপনীয়তা নীতি সংশোধন বা হালনাগাদ করার অধিকার সংরক্ষণ করে। কোনো বড় পরিবর্তন আনা হলে তা অ্যাপ আপডেট রিলিজ নোট বা অ্যাপের ভেতরে স্পষ্টভাবে জানিয়ে দেওয়া হবে।")
                }
            )

            // Clause 10
            PolicySectionCard(
                numberBengali = "১০",
                titleBengali = "যোগাযোগ ও সহায়তা",
                icon = Icons.Outlined.Email,
                content = buildAnnotatedString {
                    append("প্রাইভেসি পলিসি, ডেটা নিরাপত্তা বা অ্যাপ সংক্রান্ত যেকোনো প্রশ্ন কিংবা পরামর্শের জন্য সরাসরি ইমেইলে যোগাযোগ করতে পারেন:\n\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GoldPrimary)) {
                        append("✉ hmibrahimsarkar712@gmail.com")
                    }
                },
                onClick = {
                    openEmail(context, "hmibrahimsarkar712@gmail.com", "কাব্যলোকের ব্রহ্মকবি - প্রাইভেসি পলিসি সংক্রান্ত")
                }
            )

            // ================= 4. WARM POETIC CLOSING CARD =================
            ElevatedGlassCard(
                borderColor = GoldPrimary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            imageVector = Icons.Outlined.FormatQuote,
                            contentDescription = "Quote",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "“আপনার লেখা, আপনার গোপন কথা — এই অ্যাপে সবসময় শুধু আপনারই থাকবে।”",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = AppTitleFont,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "— কাব্যলোকের ব্রহ্মকবি পরিবার",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ================= REUSABLE DESIGN SYSTEM COMPONENTS =================

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
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    numberBengali: String,
    titleBengali: String,
    icon: ImageVector,
    content: CharSequence,
    onClick: (() -> Unit)? = null
) {
    ElevatedGlassCard(
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(GoldLight, GoldPrimary, GoldDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numberBengali,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Tinted Icon Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.15f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title
                Text(
                    text = titleBengali,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppTitleFont,
                    color = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            if (content is androidx.compose.ui.text.AnnotatedString) {
                Text(
                    text = content,
                    fontSize = 13.5.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
                    text = content.toString(),
                    fontSize = 13.5.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SummaryBulletItem(
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 19.sp
        )
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
