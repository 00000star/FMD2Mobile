package com.fmd2mobile.parser.source.mangadex

import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.source.MangaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class MangaDexSource @Inject constructor(private val client: OkHttpClient) : MangaSource {
    override val name: String = "MangaDex"
    override val baseUrl: String = "https://mangadex.org"
    private val apiUrl: String = "https://api.mangadex.org"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: throw IOException("Empty body")
        }
    }

    private fun parseMangaData(data: MdMangaData): Manga {
        val title = data.attributes.title["en"] ?: data.attributes.title.values.firstOrNull() ?: "Unknown Title"
        val description = data.attributes.description?.get("en") ?: ""

        var author = ""
        var coverFileName = ""

        data.relationships.forEach { rel ->
            when (rel.type) {
                "author" -> author = rel.attributes?.name ?: ""
                "cover_art" -> coverFileName = rel.attributes?.fileName ?: ""
            }
        }

        val thumbnailUrl = if (coverFileName.isNotEmpty()) {
            "https://uploads.mangadex.org/covers/${data.id}/$coverFileName"
        } else {
            ""
        }

        return Manga(
            title = title,
            author = author,
            description = description,
            thumbnailUrl = thumbnailUrl,
            source = name,
            url = data.id // Store MangaDex UUID as url
        )
    }

    override suspend fun search(query: String, page: Int): List<Manga> {
        val offset = (page - 1) * 20
        val url = "$apiUrl/manga".toHttpUrl().newBuilder()
            .addQueryParameter("title", query)
            .addQueryParameter("limit", "20")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("includes[]", "cover_art")
            .addQueryParameter("includes[]", "author")
            .build()
            .toString()

        val response = get(url)
        val listResponse = json.decodeFromString<MdMangaListResponse>(response)
        return listResponse.data.map { parseMangaData(it) }
    }

    override suspend fun getLatestUpdates(page: Int): List<Manga> {
        val offset = (page - 1) * 20
        val url = "$apiUrl/manga".toHttpUrl().newBuilder()
            .addQueryParameter("limit", "20")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("includes[]", "cover_art")
            .addQueryParameter("includes[]", "author")
            .addQueryParameter("order[latestUploadedChapter]", "desc")
            .build()
            .toString()

        val response = get(url)
        val listResponse = json.decodeFromString<MdMangaListResponse>(response)
        return listResponse.data.map { parseMangaData(it) }
    }

    override suspend fun getPopular(page: Int): List<Manga> {
        val offset = (page - 1) * 20
        val url = "$apiUrl/manga".toHttpUrl().newBuilder()
            .addQueryParameter("limit", "20")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("includes[]", "cover_art")
            .addQueryParameter("includes[]", "author")
            .addQueryParameter("order[followedCount]", "desc")
            .build()
            .toString()

        val response = get(url)
        val listResponse = json.decodeFromString<MdMangaListResponse>(response)
        return listResponse.data.map { parseMangaData(it) }
    }

    override suspend fun getMangaDetails(manga: Manga): Manga {
        val url = "$apiUrl/manga/${manga.url}".toHttpUrl().newBuilder()
            .addQueryParameter("includes[]", "cover_art")
            .addQueryParameter("includes[]", "author")
            .build()
            .toString()

        val response = get(url)
        val mdMangaResponse = json.decodeFromString<MdMangaResponse>(response)
        
        // Retain local DB id and favorite status
        val updatedManga = parseMangaData(mdMangaResponse.data)
        return updatedManga.copy(
            id = manga.id,
            isFavorite = manga.isFavorite
        )
    }

    override suspend fun getChapterList(manga: Manga): List<Chapter> {
        val url = "$apiUrl/manga/${manga.url}/feed".toHttpUrl().newBuilder()
            .addQueryParameter("limit", "500")
            .addQueryParameter("translatedLanguage[]", "en")
            .addQueryParameter("order[chapter]", "desc")
            .build()
            .toString()

        val response = get(url)
        val listResponse = json.decodeFromString<MdChapterListResponse>(response)
        
        return listResponse.data.map { chapterData ->
            val number = chapterData.attributes.chapter?.toFloatOrNull() ?: -1f
            val title = chapterData.attributes.title?.takeIf { it.isNotBlank() } 
                ?: "Chapter ${chapterData.attributes.chapter ?: "?"}"

            Chapter(
                mangaId = manga.id,
                number = number,
                title = title,
                url = chapterData.id // Store Chapter UUID as URL
            )
        }
    }

    override suspend fun getPageList(chapter: Chapter): List<String> {
        val url = "$apiUrl/at-home/server/${chapter.url}"
        val response = get(url)
        val atHomeResponse = json.decodeFromString<MdAtHomeResponse>(response)
        
        val baseUrl = atHomeResponse.baseUrl
        val hash = atHomeResponse.chapter.hash
        
        return atHomeResponse.chapter.data.map { filename ->
            "$baseUrl/data/$hash/$filename"
        }
    }
}
