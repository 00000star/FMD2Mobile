package com.fmd2mobile.parser.extension

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.fmd2mobile.core.source.MangaSource
import com.fmd2mobile.core.source.SourceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dalvik.system.DexClassLoader
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sourceManager: SourceManager
) {
    fun loadExtensions() {
        val extensionDir = File(context.getExternalFilesDir(null), "extensions")
        if (!extensionDir.exists()) {
            extensionDir.mkdirs()
            return
        }

        val apkFiles = extensionDir.listFiles { file -> file.extension.equals("apk", ignoreCase = true) } ?: emptyArray()

        for (apk in apkFiles) {
            try {
                loadApk(apk)
            } catch (e: Exception) {
                Log.e("ExtensionManager", "Failed to load ${apk.name}", e)
            }
        }
    }

    private fun loadApk(apk: File) {
        val pm = context.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(apk.absolutePath, PackageManager.GET_META_DATA)
            ?: throw Exception("Cannot get package info from $apk")

        val appInfo = pkgInfo.applicationInfo
        appInfo.sourceDir = apk.absolutePath
        appInfo.publicSourceDir = apk.absolutePath

        val metaData = appInfo.metaData
            ?: throw Exception("No metaData found in AndroidManifest for $apk")
            
        val className = metaData.getString("fmd2.extension.class")
            ?: throw Exception("No 'fmd2.extension.class' metadata found in $apk")

        val optimizedDir = context.getDir("dex", Context.MODE_PRIVATE)

        val classLoader = DexClassLoader(
            apk.absolutePath,
            optimizedDir.absolutePath,
            null,
            context.classLoader
        )

        val sourceClass = classLoader.loadClass(className)
        val sourceInstance = sourceClass.getDeclaredConstructor().newInstance() as MangaSource

        sourceManager.registerDynamicSource(sourceInstance)
        Log.i("ExtensionManager", "Successfully loaded dynamic extension: ${sourceInstance.name}")
    }
}
