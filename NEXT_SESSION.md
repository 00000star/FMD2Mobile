# Next Session Goals: FMD2 Mobile

This document outlines the focus and priority tasks for the next development iteration.

## Priority 1: Advanced Reader Experience
- Implement Webtoon (Vertical Scroll) and Manga (Right-to-Left) reading modes.
- Add pinch-to-zoom, panning, and double-tap zoom functionality to the image viewer.

## Priority 2: Source Extension SDK
- Create a standalone template repository and SDK library (`fmd2-extension-sdk`) for developers to build `.apk` scraper plugins.
- Define a secure IPC/Interface contract for external plugins to communicate with the core app.

## Priority 3: Cloudflare Bypass Integration
- Integrate OkHttp interceptors with WebView or a JavaScript evaluator to automatically solve Cloudflare Turnstile/CAPTCHA challenges for protected sources.
