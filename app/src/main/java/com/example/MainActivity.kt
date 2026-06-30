package com.example

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

enum class ThemeColor(val color: Color, val displayName: String) {
    NeonGreen(Color(0xFF00FF87), "Neon Green"),
    Blue(Color(0xFF2196F3), "Blue"),
    Red(Color(0xFFF44336), "Red"),
    Purple(Color(0xFF9C27B0), "Purple"),
    Orange(Color(0xFFFF9800), "Orange")
}

fun getDynamicDarkColorScheme(primaryColor: Color) = darkColorScheme(
    primary = primaryColor,
    onPrimary = Color.Black,
    primaryContainer = primaryColor.copy(alpha = 0.3f),
    onPrimaryContainer = primaryColor,
    secondary = Color(0xFFB388FF),
    onSecondary = Color(0xFF22005D),
    secondaryContainer = Color(0xFF380099),
    onSecondaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF09090B),
    onBackground = Color(0xFFFAFAFA),
    surface = Color(0xFF121214),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF333338),
    outlineVariant = Color(0xFF27272A)
)

fun getDynamicLightColorScheme(primaryColor: Color) = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    primaryContainer = primaryColor.copy(alpha = 0.3f),
    onPrimaryContainer = primaryColor,
    secondary = Color(0xFF65558F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    background = Color(0xFFF7F7F9),
    onBackground = Color(0xFF09090B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF09090B),
    surfaceVariant = Color(0xFFF1F1F4),
    onSurfaceVariant = Color(0xFF52525B),
    outline = Color(0xFFD4D4D8),
    outlineVariant = Color(0xFFE4E4E7)
)

@Composable
fun SleekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: ThemeColor = ThemeColor.NeonGreen,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) getDynamicDarkColorScheme(themeColor.color) else getDynamicLightColorScheme(themeColor.color)
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

enum class ScriptCategory {
    Universal, KeyboardEscape
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("UniversalHubPrefs", Context.MODE_PRIVATE)

    var currentTab by mutableStateOf(ScreenTab.Scripts)

    var themeMode by mutableStateOf(ThemeMode.valueOf(prefs.getString("themeMode", ThemeMode.System.name) ?: ThemeMode.System.name))
        private set
    
    var themeColor by mutableStateOf(ThemeColor.valueOf(prefs.getString("themeColor", ThemeColor.NeonGreen.name) ?: ThemeColor.NeonGreen.name))
        private set
        
    var autoExecute by mutableStateOf(prefs.getBoolean("autoExecute", true))
        private set

    var safeMode by mutableStateOf(prefs.getBoolean("safeMode", false))
        private set

    var notifications by mutableStateOf(prefs.getBoolean("notifications", true))
        private set
        
    fun updateThemeMode(mode: ThemeMode) {
        themeMode = mode
        prefs.edit().putString("themeMode", mode.name).apply()
    }

    fun updateThemeColor(color: ThemeColor) {
        themeColor = color
        prefs.edit().putString("themeColor", color.name).apply()
    }

    fun updateAutoExecute(enabled: Boolean) {
        autoExecute = enabled
        prefs.edit().putBoolean("autoExecute", enabled).apply()
    }

    fun updateSafeMode(enabled: Boolean) {
        safeMode = enabled
        prefs.edit().putBoolean("safeMode", enabled).apply()
    }

    fun updateNotifications(enabled: Boolean) {
        notifications = enabled
        prefs.edit().putBoolean("notifications", enabled).apply()
    }
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

            SleekTheme(darkTheme = isDark, themeColor = viewModel.themeColor) {
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
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: ScriptCategory,
    onCategorySelected: (ScriptCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryTab(
            text = "Universal Scripts",
            isSelected = selectedCategory == ScriptCategory.Universal,
            modifier = Modifier.weight(1f),
            onClick = { onCategorySelected(ScriptCategory.Universal) }
        )
        CategoryTab(
            text = "Keyboard Escape",
            isSelected = selectedCategory == ScriptCategory.KeyboardEscape,
            modifier = Modifier.weight(1f),
            onClick = { onCategorySelected(ScriptCategory.KeyboardEscape) }
        )
    }
}

@Composable
fun CategoryTab(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "TabBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "TabContent"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ScriptsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf(ScriptCategory.Universal) }

    val v2Script = "loadstring(game:HttpGet(\"https://raw.githubusercontent.com/ErdemProHaHaHack/Universal-hub-v2/refs/heads/main/script\"))()"
    val v1Script = "loadstring(game:HttpGet(\"https://raw.githubusercontent.com/ErdemProHaHaHack/universal-script/refs/heads/main/script\"))()"
    val kbScript = "loadstring(game:HttpGet(\"https://raw.githubusercontent.com/ErdemProHaHaHack/Keyboard-Escape-Scripts/refs/heads/main/Win%20Farm\"))()"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection(title = "Scripts", icon = Icons.Filled.Build)

        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(targetState = selectedCategory, label = "ScriptCategoryCrossfade") { category ->
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                when (category) {
                    ScriptCategory.Universal -> {
                        SectionTitle("VERSION 2 (BETA)")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolScriptCard(content = v2Script, onCopy = { copyToClipboard(context, "V2 Script", v2Script) })

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionTitle("VERSION 1")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolScriptCard(content = v1Script, onCopy = { copyToClipboard(context, "V1 Script", v1Script) })
                    }
                    ScriptCategory.KeyboardEscape -> {
                        SectionTitle("WIN FARM")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolScriptCard(content = kbScript, onCopy = { copyToClipboard(context, "KB Escape Script", kbScript) })
                    }
                }
            }
        }
    }
}

@Composable
fun KeysScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf(ScriptCategory.Universal) }

    val v2Key = "woahmyuniversalscriptissoopv2"
    val v1Key = "woahmyuniversalscriptissoop"
    val kbKey = "myfirstspecialgamescript"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection(title = "Access Keys", icon = Icons.Filled.Lock)

        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(targetState = selectedCategory, label = "KeyCategoryCrossfade") { category ->
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                when (category) {
                    ScriptCategory.Universal -> {
                        SectionTitle("VERSION 2 (BETA)")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolKeyCard(content = v2Key, onCopy = { copyToClipboard(context, "V2 Key", v2Key) })

                        Spacer(modifier = Modifier.height(32.dp))

                        SectionTitle("VERSION 1")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolKeyCard(content = v1Key, onCopy = { copyToClipboard(context, "V1 Key", v1Key) })
                    }
                    ScriptCategory.KeyboardEscape -> {
                        SectionTitle("KEYBOARD ESCAPE KEY")
                        Spacer(modifier = Modifier.height(12.dp))
                        CoolKeyCard(content = kbKey, onCopy = { copyToClipboard(context, "KB Escape Key", kbKey) })
                    }
                }
            }
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
        HeaderSection(title = "Executors", icon = Icons.Filled.PlayArrow)

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
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
fun SettingsScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        HeaderSection(title = "Settings", icon = Icons.Filled.Settings)

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            
            SectionTitle("APPEARANCE")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            ) {
                Column {
                    ThemeOptionRow(
                        label = "System Default",
                        isSelected = viewModel.themeMode == ThemeMode.System,
                        onClick = { viewModel.updateThemeMode(ThemeMode.System) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    ThemeOptionRow(
                        label = "Light Mode",
                        isSelected = viewModel.themeMode == ThemeMode.Light,
                        onClick = { viewModel.updateThemeMode(ThemeMode.Light) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    ThemeOptionRow(
                        label = "Dark Mode",
                        isSelected = viewModel.themeMode == ThemeMode.Dark,
                        onClick = { viewModel.updateThemeMode(ThemeMode.Dark) }
                    )
                }
            }

            SectionTitle("BUTTON COLORS")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose App Theme Color",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ThemeColor.values().forEach { themeColor ->
                            ColorPickerItem(
                                color = themeColor.color,
                                isSelected = viewModel.themeColor == themeColor,
                                onClick = { viewModel.updateThemeColor(themeColor) }
                            )
                        }
                    }
                }
            }

            SectionTitle("PREFERENCES")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            ) {
                Column {
                    SwitchSettingRow(
                        label = "Auto Execute Scripts",
                        description = "Automatically copy and execute on tap",
                        checked = viewModel.autoExecute,
                        onCheckedChange = { viewModel.updateAutoExecute(it) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    SwitchSettingRow(
                        label = "Safe Mode",
                        description = "Enable anti-ban protection features",
                        checked = viewModel.safeMode,
                        onCheckedChange = { viewModel.updateSafeMode(it) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    SwitchSettingRow(
                        label = "Notifications",
                        description = "Show execution alerts",
                        checked = viewModel.notifications,
                        onCheckedChange = { viewModel.updateNotifications(it) }
                    )
                }
            }
            
            SectionTitle("ABOUT")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Universal Hub App v1.2.0",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Developed by ErdemPro655. All scripts and executors provided are for educational purposes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ColorPickerItem(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected Color",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun SwitchSettingRow(label: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
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
fun HeaderSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Universal Hub by ErdemPro655",
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
                    icon = Icons.AutoMirrored.Filled.List,
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
