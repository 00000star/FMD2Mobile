package com.fmd2mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.ui.viewmodel.MangaDetailsViewModel

/**
 * Manga details page showing synopsis, status, cover metadata, and list of chapters.
 * Allows initiating downloads and reading chapters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    mangaId: Long,
    onNavigateToReader: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MangaDetailsViewModel = hiltViewModel()
) {
    val manga by viewModel.manga.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(manga?.title ?: "Loading...", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        )
                    )
                )
        ) {
            val currentManga = manga
            if (currentManga == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFA855F7))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                model = currentManga.thumbnailUrl,
                                contentDescription = currentManga.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentManga.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Author: ${currentManga.author}",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "Artist: ${currentManga.artist}",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "Source: ${currentManga.source}",
                                    fontSize = 14.sp,
                                    color = Color(0xFFA855F7),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Synopsis
                    item {
                        Column {
                            Text(
                                text = "Synopsis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentManga.description.ifEmpty { "No description available." },
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Chapters Title
                    item {
                        Text(
                            text = "Chapters (${chapters.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Chapters list
                    items(chapters, key = { it.id }) { chapter ->
                        ChapterListItem(
                            chapter = chapter,
                            onClick = { onNavigateToReader(chapter.id) },
                            onDownloadClick = { viewModel.downloadChapter(chapter) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterListItem(
    chapter: Chapter,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = chapter.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Chapter ${chapter.number}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Download Status icon / action button
            IconButton(onClick = onDownloadClick) {
                when (chapter.status) {
                    Chapter.Status.NOT_DOWNLOADED -> Icon(
                        Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.LightGray
                    )
                    Chapter.Status.DOWNLOADING -> Icon(
                        Icons.Default.Downloading,
                        contentDescription = "Downloading",
                        tint = Color(0xFFA855F7)
                    )
                    Chapter.Status.DOWNLOADED -> Icon(
                        Icons.Default.DownloadDone,
                        contentDescription = "Downloaded",
                        tint = Color.Green
                    )
                    Chapter.Status.FAILED -> Icon(
                        Icons.Default.Download,
                        contentDescription = "Retry",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
