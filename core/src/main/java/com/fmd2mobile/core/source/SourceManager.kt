package com.fmd2mobile.core.source

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages both built-in sources (provided via Dagger Multibindings)
 * and dynamically loaded extension sources.
 */
@Singleton
class SourceManager @Inject constructor(
    private val builtInSources: Map<String, @JvmSuppressWildcards MangaSource>
) {
    private val dynamicSources = mutableMapOf<String, MangaSource>()

    fun registerDynamicSource(source: MangaSource) {
        dynamicSources[source.name] = source
    }

    fun getSources(): Map<String, MangaSource> {
        return builtInSources + dynamicSources
    }

    fun getSource(name: String): MangaSource? {
        return dynamicSources[name] ?: builtInSources[name]
    }
}
