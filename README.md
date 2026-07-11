# FMD2 Mobile for Android

FMD2 Mobile is an open-source Android application inspired by **Free Manga Downloader 2 (FMD2)**. It is a native, modern Android application developed using **Kotlin**, **Jetpack Compose**, **Material 3**, and **Hilt** that downloads manga/comics from online sources and saves them in Mihon's Local Source format for offline reading.

---

## Key Features

- **Multi-Source Scraping**: HTML parsing utilizing Jsoup with Cloudflare bypass indicators and rate-limiting.
- **ADM-Level Parallel Downloader**: 
  - Dynamic chunking (4-8 chunks per page).
  - Parallel segmented downloading using HTTP Range headers.
  - Chunk progress persistence in Room to support pause and resume.
  - Exponential backoff retry logic.
  - Multi-task queuing with concurrency throttles.
- **Mihon Local Source Export**: 
  - Automatically compiles downloaded folders into zip CBZ archives with stored compression (uncompressed zip entries + CRC32 verification).
  - Automatically sets up `cover.jpg` for each series folder.
  - Notifies Android's MediaScanner to refresh Mihon local source indexes.
- **Advanced Manga Reader**: 
  - Horizontal LTR/RTL paging and Continuous Vertical layouts.
  - Double-tap and pinch-to-zoom gestures.
  - Screen brightness overrides and wake-lock controls.
- **Local Database**: Stores reading histories, category tags, library favorites, and page progresses.

---

## Architecture Design

The project is structured with 7 modular layers to maximize separation of concerns and build modularity:

1. `:app` - Jetpack Compose UI layout, view models, and navigation logic.
2. `:core` - Platform-agnostic domain models and repository/service interfaces.
3. `:database` - Room DB implementation, cross-ref tables, and database repositories.
4. `:downloader` - WorkManager worker and multi-thread parallel downloader.
5. `:parser` - OkHttp client configuration and Jsoup HTML web scraping sources.
6. `:localsource` - Uncompressed CBZ packaging and Mihon layout formatting.
7. `:settings` - DataStore Preferences configuration.

---

## Developer Setup

### Importing to Android Studio
1. Open Android Studio.
2. Select **Open An Existing Project** and navigate to this folder.
3. Sync the Gradle files (`libs.versions.toml` version catalog will automatically configure versions).

### Compiling Production App
Refer to the complete guide in [RELEASE_INSTRUCTIONS.md](file:///sdcard/Antigravity_Projects/FMD2Mobile/RELEASE_INSTRUCTIONS.md).
