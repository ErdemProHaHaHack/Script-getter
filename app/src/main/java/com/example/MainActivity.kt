package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

val SleekDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF99F5FF),
    secondary = Color(0xFFB388FF),
    onSecondary = Color(0xFF22005D),
    secondaryContainer = Color(0xFF380099),
    onSecondaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF0A0A0C),
    onBackground = Color(0xFFFAFAFA),
    surface = Color(0xFF141418),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF1F1F24),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF333338),
    outlineVariant = Color(0xFF27272A)
)

val SleekLightColorScheme = lightColorScheme(
    primary = Color(0xFF006874),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF65558F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    background = Color(0xFFF7F7F9),
    onBackground = Color(0xFF0A0A0C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0A0A0C),
    surfaceVariant = Color(0xFFF1F1F4),
    onSurfaceVariant = Color(0xFF52525B),
    outline = Color(0xFFD4D4D8),
    outlineVariant = Color(0xFFE4E4E7)
)

@Composable
fun SleekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SleekDarkColorScheme else SleekLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

enum class ScreenTab {
    Scripts, Keys, Executors, Settings
}

enum class ThemeMode {
    System, Light, Dark
}

class MainViewModel : ViewModel() {
    var currentTab by mutableStateOf(ScreenTab.Scripts)
    var themeMode by mutableStateOf(ThemeMode.System)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isSystemDark = isSystemInDarkTheme()
            val isDark = when(viewModel.themeMode) {
                ThemeMode.System -> isSystemDark
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            SleekTheme(darkTheme = isDark) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            currentTab = viewModel.currentTab,
                            onTabSelected = { viewModel.currentTab = it }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.Transparent
                    ) {
                        when (viewModel.currentTab) {
                            ScreenTab.Scripts -> ScriptsScreen()
                            ScreenTab.Keys -> KeysScreen()
                            ScreenTab.Executors -> ExecutorsScreen()
                            ScreenTab.Settings -> SettingsScreen(
                                currentTheme = viewModel.themeMode,
                                onThemeChanged = { viewModel.themeMode = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val v2Script = "loadstring(game:HttpGet(\"https://raw.githubusercontent.com/ErdemProHaHaHack/Universal-hub-v2/refs/heads/main/script\"))()"
    val v1Script = "loadstring(game:HttpGet(\"https://raw.githubusercontent.com/ErdemProHaHaHack/universal-script/refs/heads/main/script\"))()"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection()

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("VERSION 1")
            Spacer(modifier = Modifier.height(12.dp))
            CoolScriptCard(content = v1Script, onCopy = { copyToClipboard(context, "V1 Script", v1Script) })

            Spacer(modifier = Modifier.height(40.dp))

            SectionTitle("VERSION 2 (BETA)")
            Spacer(modifier = Modifier.height(12.dp))
            CoolScriptCard(content = v2Script, onCopy = { copyToClipboard(context, "V2 Script", v2Script) })
        }
    }
}

@Composable
fun KeysScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val v2Key = "woahmyuniversalscriptissoopv2"
    val v1Key = "woahmyuniversalscriptissoop"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection()

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("VERSION 1")
            Spacer(modifier = Modifier.height(12.dp))
            CoolKeyCard(content = v1Key, onCopy = { copyToClipboard(context, "V1 Key", v1Key) })

            Spacer(modifier = Modifier.height(40.dp))

            SectionTitle("VERSION 2 (BETA)")
            Spacer(modifier = Modifier.height(12.dp))
            CoolKeyCard(content = v2Key, onCopy = { copyToClipboard(context, "V2 Key", v2Key) })
        }
    }
}

@Composable
fun ExecutorsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val globalUrl = "https://www.mediafire.com/file/21frzxbp4zmvw7t/Delta_Global-2.725-UpdatedbyErdemPro655.apk/file"
    val vnUrl = "https://www.mediafire.com/file/xb9wlouzedpkjkt/Delta_Vng-2.725-UpdatedbyErdemPro655.apk/file"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection()

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("GLOBAL VERSION")
            Spacer(modifier = Modifier.height(12.dp))
            CoolLinkCard(
                title = "DELTA GLOBAL",
                url = globalUrl,
                onCopy = { copyToClipboard(context, "Global Link", globalUrl) },
                onOpen = { openBrowser(context, globalUrl) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            SectionTitle("VN VERSION (Only Vietnam Users)")
            Spacer(modifier = Modifier.height(12.dp))
            CoolLinkCard(
                title = "DELTA VN",
                url = vnUrl,
                onCopy = { copyToClipboard(context, "VN Link", vnUrl) },
                onOpen = { openBrowser(context, vnUrl) }
            )
        }
    }
}

@Composable
fun SettingsScreen(currentTheme: ThemeMode, onThemeChanged: (ThemeMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        Text(
            text = "Theme Preference",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column {
                ThemeOptionRow(
                    label = "System Default",
                    isSelected = currentTheme == ThemeMode.System,
                    onClick = { onThemeChanged(ThemeMode.System) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                ThemeOptionRow(
                    label = "Light Mode",
                    isSelected = currentTheme == ThemeMode.Light,
                    onClick = { onThemeChanged(ThemeMode.Light) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                ThemeOptionRow(
                    label = "Dark Mode",
                    isSelected = currentTheme == ThemeMode.Dark,
                    onClick = { onThemeChanged(ThemeMode.Dark) }
                )
            }
        }
    }
}

@Composable
fun ThemeOptionRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = "Universal Hub",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Scripts by: ErdemPro655 on YT",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun CoolScriptCard(content: String, onCopy: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXECUTION SCRIPT",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(percent = 50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LOADSTRING",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = content,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCopy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Copy Script",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CoolKeyCard(content: String, onCopy: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "ACCESS KEY",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = content,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Copy Key",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentTab: ScreenTab, onTabSelected: (ScreenTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 24.dp
    ) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(bottom = 8.dp), // Navigation bar bottom padding
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavItem(
                    icon = Icons.Filled.List,
                    label = "Scripts",
                    isSelected = currentTab == ScreenTab.Scripts,
                    onClick = { onTabSelected(ScreenTab.Scripts) }
                )
                NavItem(
                    icon = Icons.Filled.Lock,
                    label = "Keys",
                    isSelected = currentTab == ScreenTab.Keys,
                    onClick = { onTabSelected(ScreenTab.Keys) }
                )
                NavItem(
                    icon = Icons.Filled.PlayArrow,
                    label = "Executors",
                    isSelected = currentTab == ScreenTab.Executors,
                    onClick = { onTabSelected(ScreenTab.Executors) }
                )
                NavItem(
                    icon = Icons.Filled.Settings,
                    label = "Settings",
                    isSelected = currentTab == ScreenTab.Settings,
                    onClick = { onTabSelected(ScreenTab.Settings) }
                )
            }
        }
    }
}

@Composable
fun NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    RoundedCornerShape(percent = 50)
                )
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun openBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Composable
fun CoolLinkCard(title: String, url: String, onCopy: () -> Unit, onOpen: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = url,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Copy Link",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Open Link",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
