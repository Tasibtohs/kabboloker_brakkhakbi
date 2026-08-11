package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
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

            // ================= 3. SHARE APP =================
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
                modifier = Modifier.fillMaxWidth().height(48.dp)
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
