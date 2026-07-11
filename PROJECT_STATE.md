# Project State: FMD2 Mobile for Android

## Current Status
All 20 developmental build strategy steps have been successfully planned, structured, and implemented inside the directory `/sdcard/Antigravity_Projects/FMD2Mobile/`.

### Built Modules
- `:app` (View models, UI screens, Main Activity and bottom navigation).
- `:core` (Domain models, repository interfaces, and downloader interfaces).
- `:database` (Room database integration, entities, DAOs, and repository implementations).
- `:downloader` (WorkManager parallel chunk downloader and speed tracker).
- `:parser` (Jsoup-based HTML source scraper + MangaDex API JSON implementation). Dagger Multibindings enabled for multi-source support.
- `:localsource` (Mihon Local Source export zip/CBZ formatter).
- `:settings` (DataStore preferences management).
- `Plugin Architecture` (Dynamic `DexClassLoader` extension manager to load external `.apk` scrapers at runtime without recompiling).

---

## Technical Specifications
- **SDK**: compileSdk 35, minSdk 26
- **Architecture**: Clean Architecture + MVVM + Modularization
- **UI**: Jetpack Compose + Material 3
- **Local Storage**: Room DB + DataStore Preferences
- **Networking**: OkHttp + Jsoup Scrapers
- **Background Tasks**: WorkManager
- **DI**: Hilt Dependency Injection
- **Image Loader**: Coil
