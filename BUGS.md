# Bug Log: FMD2 Mobile

Tracking currently identified or theoretical issues in FMD2 Mobile.

## 1. Media Scanner Scan Action Restrictions on Modern Android
- **Category**: Storage / System Intents
- **Severity**: Low
- **Description**: Broad-casting `Intent.ACTION_MEDIA_SCANNER_SCAN_FILE` is deprecated on Android 10+ and may be throttled or ignored by modern media scan systems.
- **Remediation**: Use `MediaScannerConnection.scanFile(context, paths, mimeTypes, callback)` as a modern, reliable replacement.

## 2. Semaphore Lock Cancellation Handling
- **Category**: Threading / Concurrency
- **Severity**: Medium
- **Description**: When a download is cancelled or paused, any worker thread waiting on the concurrency semaphore may not release its token properly if cancellation is blocked.
- **Remediation**: Wrap permit logic inside `try-finally` blocks to guarantee release of semaphore permits during coroutine cancellations.
