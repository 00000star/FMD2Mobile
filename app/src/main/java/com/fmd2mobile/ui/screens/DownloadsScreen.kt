package com.fmd2mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadStatus
import com.fmd2mobile.ui.viewmodel.DownloadsViewModel

/**
 * Downloads Screen displaying active downloads, queues, speed metrics,
 * and buttons to pause, resume, or cancel jobs.
 */
@Composable
fun DownloadsScreen(
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val downloads by viewModel.downloads.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF022C22), // Dark Green
                        Color(0xFF0F172A)  // Slate 900
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Downloads",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Global Controls
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.pauseAll() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause All", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.resumeAll() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume All", tint = Color.White)
                    }
                }
            }

            // List Content
            if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active downloads", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(downloads, key = { it.id }) { download ->
                        DownloadItemRow(
                            download = download,
                            onCancelClick = { viewModel.cancelDownload(download.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    download: Download,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chapter ID: ${download.chapterId}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Status: ${download.status.name}",
                        fontSize = 12.sp,
                        color = when (download.status) {
                            DownloadStatus.DOWNLOADING -> Color.Green
                            DownloadStatus.PAUSED -> Color.Yellow
                            DownloadStatus.FAILED -> Color.Red
                            else -> Color.Gray
                        }
                    )
                }
                
                IconButton(onClick = onCancelClick) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { download.progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFA855F7), // Purple track
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${download.progress}%",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                if (download.status == DownloadStatus.DOWNLOADING) {
                    Text(
                        text = formatSpeed(download.speed),
                        fontSize = 12.sp,
                        color = Color(0xFFA855F7),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatSpeed(bytesPerSec: Long): String {
    val kb = bytesPerSec / 1024.0
    val mb = kb / 1024.0
    return when {
        mb >= 1.0 -> String.format("%.1f MB/s", mb)
        kb >= 1.0 -> String.format("%.1f KB/s", kb)
        else -> "$bytesPerSec B/s"
    }
}
