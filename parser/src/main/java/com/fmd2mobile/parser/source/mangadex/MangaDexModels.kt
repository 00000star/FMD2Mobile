package com.fmd2mobile.parser.source.mangadex

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MdMangaListResponse(
    val data: List<MdMangaData>
)

@Serializable
data class MdMangaResponse(
    val data: MdMangaData
)

@Serializable
data class MdMangaData(
    val id: String,
    val type: String,
    val attributes: MdMangaAttributes,
    val relationships: List<MdRelationship> = emptyList()
)

@Serializable
data class MdMangaAttributes(
    val title: Map<String, String>,
    val description: Map<String, String>? = null,
    val altTitles: List<Map<String, String>>? = null
)

@Serializable
data class MdRelationship(
    val id: String,
    val type: String,
    val attributes: MdRelationshipAttributes? = null
)

@Serializable
data class MdRelationshipAttributes(
    val fileName: String? = null,
    val name: String? = null
)

@Serializable
data class MdChapterListResponse(
    val data: List<MdChapterData>
)

@Serializable
data class MdChapterData(
    val id: String,
    val type: String,
    val attributes: MdChapterAttributes
)

@Serializable
data class MdChapterAttributes(
    val volume: String? = null,
    val chapter: String? = null,
    val title: String? = null,
    val translatedLanguage: String? = null
)

@Serializable
data class MdAtHomeResponse(
    val baseUrl: String,
    val chapter: MdAtHomeChapter
)

@Serializable
data class MdAtHomeChapter(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)
