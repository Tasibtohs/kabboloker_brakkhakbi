package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmibrahimsarkar.kabboloker_brakkhakbi.R
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppTitleFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.AppBodyFont
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.DrawerShape
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.Screen
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldDark
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldLight
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.GoldPrimary
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme.SoftLavender

@Composable
fun NavigationDrawerContent(
    currentScreen: Screen,
    isDarkMode: Boolean,
    onNavigate: (Screen) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onCloseDrawer: () -> Unit,
    onExportAllPdf: () -> Unit = {}
) {
    val bg = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .clip(DrawerShape)
            .background(bg)
            .padding(vertical = 24.dp)
    ) {
        // Drawer Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GoldLight.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.poetry_app_logo_1785492330798),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    val goldBrush = Brush.horizontalGradient(
                        colors = listOf(GoldLight, GoldPrimary, GoldDark)
                    )
                    Text(
                        text = "কাব্যলোকের ব্রক্ষকবি",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppTitleFont,
                        style = MaterialTheme.typography.titleLarge.copy(brush = goldBrush)
                    )
                    Text(
                        text = "কবি ও শব্দের আসর",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = AppBodyFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Menu items list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            // ================= SECTION 1: "নোটস" =================
            DrawerSectionHeader(title = "নোটস")

            DrawerMenuItem(
                icon = Icons.Outlined.Description,
                label = "সব নোট",
                isSelected = currentScreen is Screen.NotesList,
                onClick = {
                    onNavigate(Screen.NotesList)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Folder,
                label = "গ্রুপসমূহ",
                isSelected = currentScreen is Screen.Groups,
                onClick = {
                    onNavigate(Screen.Groups)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.PushPin,
                label = "পিন করা নোট",
                isSelected = currentScreen is Screen.PinnedNotes,
                onClick = {
                    onNavigate(Screen.PinnedNotes)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Lock,
                label = "হাইডেন নোটস",
                isSelected = currentScreen is Screen.HiddenNotes,
                onClick = {
                    onNavigate(Screen.HiddenNotes)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Delete,
                label = "ট্র্যাশ",
                isSelected = currentScreen is Screen.Trash,
                onClick = {
                    onNavigate(Screen.Trash)
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // ================= SECTION 2: "টুলস" =================
            DrawerSectionHeader(title = "টুলস")

            DrawerMenuItem(
                icon = Icons.Outlined.PictureAsPdf,
                label = "সব নোট PDF করুন",
                isSelected = false,
                onClick = {
                    onExportAllPdf()
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Backup,
                label = "ব্যাকআপ ও রিস্টোর",
                isSelected = currentScreen is Screen.BackupRestore,
                onClick = {
                    onNavigate(Screen.BackupRestore)
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // ================= SECTION 3: "কাস্টমাইজেশন" =================
            DrawerSectionHeader(title = "কাস্টমাইজেশন")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(14.dp))
                    Icon(
                        imageVector = if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                        contentDescription = "Theme Icon",
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = if (isDarkMode) "ডার্ক মোড সক্রিয়" else "লাইট মোড সক্রিয়",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = onSurface
                    )
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleDarkMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GoldLight,
                        checkedTrackColor = GoldDark.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // ================= SECTION 4: "সেটিংস ও তথ্য" =================
            DrawerSectionHeader(title = "সেটিংস ও তথ্য")

            DrawerMenuItem(
                icon = Icons.Outlined.Settings,
                label = "সেটিংস",
                isSelected = currentScreen is Screen.Settings,
                onClick = {
                    onNavigate(Screen.Settings)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Info,
                label = "অ্যাপ সম্পর্কে",
                isSelected = currentScreen is Screen.About,
                onClick = {
                    onNavigate(Screen.About)
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Outlined.Shield,
                label = "প্রাইভেসি পলিসি",
                isSelected = currentScreen is Screen.PrivacyPolicy,
                onClick = {
                    onNavigate(Screen.PrivacyPolicy)
                    onCloseDrawer()
                }
            )
        }

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "সংস্করণ ১.০.০ • ১০০% অফলাইন",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = GoldPrimary,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) GoldLight.copy(alpha = 0.15f) else Color.Transparent
    val textTint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurface
    val iconTint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
            )
            Spacer(modifier = Modifier.width(10.dp))
        } else {
            Spacer(modifier = Modifier.width(14.dp))
        }

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textTint
        )
    }
}
