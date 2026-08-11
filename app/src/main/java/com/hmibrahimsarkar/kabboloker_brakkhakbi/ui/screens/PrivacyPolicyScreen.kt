package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SignalWifiOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AppTopBar
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AmberAccent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(listOf(GoldLight, GoldPrimary, GoldDark)),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "১০০% অফলাইন ও নিরাপদ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "আপনার কোনো তথ্য ইন্টারনেটে আদান-প্রদান করা হয় না",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Clause 1
            PolicyClauseCard(
                title = "১. তথ্যের গোপনীয়তা ও লোকাল স্টোরেজ",
                description = "‘কাব্যলোকের ব্রহ্মকবি’ অ্যাপে আপনার লিখিত সকল কবিতা, গান, গজল, সাহিত্যকর্ম বা ব্যক্তিগত নোট শুধুমাত্র আপনার নিজস্ব অ্যান্ড্রয়েড ডিভাইসের অভ্যন্তরে সুরক্ষিত লোকাল ডাটাবেসে (Room Database) সংরক্ষিত থাকে। অ্যাপ ইনস্টল থাকা অবস্থায় আপনার কোনো তথ্য বাহ্যিকভাবে দেখা সম্ভব নয়।"
            )

            // Clause 2
            PolicyClauseCard(
                title = "২. ক্লাউড বা সার্ভার সংযোগের অনুপস্থিতি",
                description = "এই অ্যাপটি সম্পূর্ণ অফলাইন-ভিত্তিক। এখানে কোনো রিমোট ব্যাকএন্ড সার্ভার, অটো-সিঙ্ক সার্ভিস বা ক্লাউড স্টোরেজ ব্যবহার করা হয়নি। অ্যাপটি ব্যবহার করার জন্য কোনো প্রকার ইন্টারনেট সংযোগের প্রয়োজন হয় না।"
            )

            // Clause 3
            PolicyClauseCard(
                title = "৩. কোনো থার্ড-পার্টি শেয়ারিং বা ট্র্যাকিং নেই",
                description = "যেহেতু অ্যাপটি কোনো অনলাইন ব্যাকএন্ডে যুক্ত নয়, তাই আপনার লেখা বিষয়বস্তু বা ব্যক্তিগত তথ্য কোনো তৃতীয় পক্ষের (Third-party) কাছে পাঠানো বা ট্র্যাকিং করা সম্পূর্ণ অসম্ভব। অ্যাপটিতে কোনো প্রকার ইউজারেজ অ্যানালিটিক্স, অ্যাড-ট্র্যাকার বা থার্ড-পার্টি সার্ভিস নেই।"
            )

            // Clause 4
            PolicyClauseCard(
                title = "৪. সিকিউরিটি ও পাসওয়ার্ড সুরক্ষা",
                description = "অ্যাপের ‘হাইডেন নোটস’ ফিচারের জন্য আপনি যে নিরাপত্তা পাসওয়ার্ড সেট করবেন, তা কেবল আপনার ফোনেই হ্যাশ (Hash) আকারে স্থানীয়ভাবে এনক্রিপ্ট হয়ে জমা থাকে। এটি অন্য কেউ উদ্ধার করতে পারে না।"
            )

            // Clause 5
            PolicyClauseCard(
                title = "৫. ডাটা ব্যাকআপ ও এক্সপোর্ট নিয়ন্ত্রণ",
                description = "নোটের ব্যাকআপ বা পিডিএফ এক্সপোর্ট ফিচার ব্যবহার করার সময় তৈরি হওয়া ফাইলগুলি কেবল আপনার নিজের ডিভাইসের পছন্দের লোকাল ফোল্ডারে সংরক্ষিত হয়। আপনি চাইলে নিজেই সেই ফাইল পরবর্তীতে অ্যাপে রিস্টোর করতে পারেন।"
            )

            // Clause 6
            PolicyClauseCard(
                title = "৬. যোগাযোগ ও সহায়তা",
                description = "প্রাইভেসি পলিসি বা অ্যাপ সংক্রান্ত যেকোনো তথ্যের জন্য সরাসরি ইমেইলে যোগাযোগ করতে পারেন: hmibrahimsarkar712@gmail.com"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicyClauseCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 13.5.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
