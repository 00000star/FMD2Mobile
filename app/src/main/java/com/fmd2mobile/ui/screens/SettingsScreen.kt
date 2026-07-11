package com.fmd2mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fmd2mobile.ui.viewmodel.SettingsViewModel

/**
 * Settings Screen displaying options to change theme, AMOLED black mode,
 * download directories, and parallel concurrent task limits.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val downloadLocation by viewModel.downloadLocation.collectAsState()
    val maxConcurrentDownloads by viewModel.maxConcurrentDownloads.collectAsState()
    val isAmoledMode by viewModel.isAmoledMode.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var locationInput by remember { mutableStateOf(downloadLocation) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        if (isAmoledMode) Color.Black else Color(0xFF1C1917), // Black or Stone
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            // Section: Appearance
            SettingsHeader("Appearance")
            
            SettingsSwitchItem(
                title = "AMOLED Black Mode",
                subtitle = "Pure black backgrounds in dark mode",
                checked = isAmoledMode,
                onCheckedChange = { viewModel.setAmoledMode(it) }
            )

            SettingsThemeItem(
                currentTheme = themeMode,
                onThemeSelected = { viewModel.setThemeMode(it) }
            )

            // Section: Downloader
            SettingsHeader("Downloader")

            SettingsClickableItem(
                title = "Download Location",
                subtitle = downloadLocation,
                onClick = {
                    locationInput = downloadLocation
                    showLocationDialog = true
                }
            )

            SettingsSliderItem(
                title = "Max Concurrent Downloads",
                subtitle = "$maxConcurrentDownloads downloads",
                value = maxConcurrentDownloads.toFloat(),
                valueRange = 1f..5f,
                onValueChangeFinished = { viewModel.setMaxConcurrentDownloads(it.toInt()) }
            )

            // Section: Info
            SettingsHeader("About")
            SettingsClickableItem(
                title = "Version",
                subtitle = "1.0.0 (FMD2 Mobile Open Source)",
                onClick = {}
            )
        }

        // Custom path dialog
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Set Download Location") },
                text = {
                    Column {
                        Text("Specify directory path where downloads are saved:")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setDownloadLocation(locationInput)
                            showLocationDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFFA855F7), // Purple Accent
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFA855F7))
        )
    }
}

@Composable
fun SettingsThemeItem(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text("Theme Mode", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("LIGHT", "DARK", "SYSTEM").forEach { theme ->
                val isSelected = currentTheme == theme
                Button(
                    onClick = { onThemeSelected(theme) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFA855F7) else Color.White.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(theme, color = if (isSelected) Color.White else Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChangeFinished: (Float) -> Unit
) {
    var sliderValue by remember(value) { mutableStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.Gray, fontSize = 14.sp)
        }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onValueChangeFinished(sliderValue) },
            valueRange = valueRange,
            steps = 3, // Permits values: 1, 2, 3, 4, 5
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFA855F7),
                activeTrackColor = Color(0xFFA855F7)
            )
        )
    }
}
