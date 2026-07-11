package com.fmd2mobile.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fmd2mobile.ui.viewmodel.ReaderViewModel

/**
 * Comic/Manga Reader screen supporting Vertical/Horizontal scrolling modes,
 * RTL/LTR, Double-Tap Zoom, Keep Screen On, and Custom Brightness.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    chapterId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Reader configuration states
    var isVerticalMode by remember { mutableStateOf(true) }
    var isRightToLeft by remember { mutableStateOf(false) } // Horizontal LTR/RTL
    var brightness by remember { mutableStateOf(0.7f) } // Default reader brightness

    // Keep Screen On Effect
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Brightness Control Effect
    LaunchedEffect(brightness) {
        activity?.let {
            val lp = it.window.attributes
            lp.screenBrightness = brightness
            it.window.attributes = lp
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reading Chapter", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit Reader", tint = Color.White)
                    }
                },
                actions = {
                    // Mode Toggle
                    IconButton(onClick = { isVerticalMode = !isVerticalMode }) {
                        Icon(
                            imageVector = if (isVerticalMode) Icons.Default.SwapHoriz else Icons.Default.SwapVert,
                            contentDescription = "Toggle Orientation",
                            tint = Color.White
                        )
                    }
                    if (!isVerticalMode) {
                        // RTL / LTR Toggle
                        TextButton(onClick = { isRightToLeft = !isRightToLeft }) {
                            Text(
                                if (isRightToLeft) "RTL" else "LTR",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.8f))
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                ReaderViewModel.ReaderUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFA855F7))
                    }
                }
                is ReaderViewModel.ReaderUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red, fontSize = 16.sp)
                    }
                }
                is ReaderViewModel.ReaderUiState.Success -> {
                    val pageCount = state.pages.size
                    if (isVerticalMode) {
                        // 1. Continuous Vertical Reader
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(state.pages) { index, page ->
                                ReaderImage(
                                    imagePath = page,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                // Progress Info Overlay
                                Text(
                                    text = "Page ${index + 1} / $pageCount",
                                    color = Color.DarkGray,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    } else {
                        // 2. Horizontal Pager Reader (Page-by-page LTR/RTL)
                        val pagerState = rememberPagerState(pageCount = { pageCount })
                        val displayPages = if (isRightToLeft) state.pages.reversed() else state.pages

                        Box(modifier = Modifier.fillMaxSize()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { pageIndex ->
                                ReaderImage(
                                    imagePath = displayPages[pageIndex],
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Progress Indicator Overlay
                            Text(
                                text = "Page ${pagerState.currentPage + 1} / $pageCount",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Image container component supporting double-tap to zoom.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderImage(
    imagePath: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
    }

    Box(
        modifier = modifier
            .transformable(state = state)
            .combinedClickable(
                onDoubleClick = {
                    scale = if (scale > 1f) 1f else 2.5f
                },
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imagePath,
            contentDescription = "Manga Page",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
        )
    }
}
