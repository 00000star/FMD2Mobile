# Next Session Goals: FMD2 Mobile

This document outlines the focus and priority tasks for the next development iteration.

## Priority 1: Multi-Source Expansion
- Integrate **MangaDex API** inside the `:parser` module as an alternative to Jsoup scrapers.
- Build structural mappers for API JSON responses to standard Domain models.

## Priority 2: Dynamic Source Loading (Plugin architecture)
- Establish interface loading utilizing DexClassLoader to fetch source parsers compiled as standalone APKs or DEX files.
- Enable adding new scrapers dynamically without recompiling the core application.

## Priority 3: Local Database Enhancements
- Support user-defined Categories with custom sorting options.
- Automatically track reading history per-page and sync read chapters.
