# Tech Debt Log: FMD2 Mobile

This document tracks identified tech debt items to resolve in future sprints.

## 1. Network Scraper Stub Fallbacks
- **Description**: `MangaReaderSource` contains inline fallback mock items to keep the application responsive and runnable when testing offline.
- **Remediation**: Extract mocks into a dedicated MockSource parser class using flavor configurations or build variations.

## 2. Hardcoded Credentials and HTTP User-Agents
- **Description**: Static `User-Agent` strings are used directly in `MangaReaderSource`.
- **Remediation**: Expose user agents as configs inside DataStore Settings.

## 3. Room Database Migration Stubs
- **Description**: Destructive migrations are enabled on Room for simplicity during step-by-step prototyping.
- **Remediation**: Build proper Room `Migration` test cases and versions.
