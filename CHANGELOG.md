# Changelog: FMD2 Mobile

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-07-11
### Added
- Multi-module directory structures (`:app`, `:core`, `:database`, `:downloader`, `:parser`, `:localsource`, `:settings`).
- Complete Version Catalog (`libs.versions.toml`) specifying dependency trees.
- Room database tables for `Mangas`, `Chapters`, `Downloads`, `DownloadChunks`, `Categories`, `History`, and `ReadingProgress`.
- Hilt dependencies and injection setups across all modules.
- Dynamic Jetpack Compose Bottom Navigation routing (Library, Browse, Downloads, Settings).
- Interface-driven `MangaSource` design and concrete Jsoup-based HTML web scraper (`MangaReaderSource`).
- ADM-level parallel chunk downloader (`ChunkDownloader`) leveraging Range headers, RandomAccessFile seek-writing, and chunk metadata saving.
- Background `DownloadWorker` utilizing WorkManager, showing ongoing notification progress, calculating speeds, and zipping final chapters.
- `CbzCreator` packing images into uncompressed zip archive entries with correct CRC32 calculation.
- `MihonExporter` saving download products into `Mihon/local/<Series>/cover.jpg` and zips, notifying Android's MediaScanner.
- Dynamic ViewModels and Compose screens for Library list, Browse queries, details profiles, page reader (continuous vertical, horizontal sliding, double-tap zoom), settings customization (AMOLED toggle, download paths, speed sliders), and download manager states.
- Unit testing configurations for CBZ creation.
- Release compilation and signed keystore instructions.
