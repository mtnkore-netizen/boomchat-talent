package com.talent.boomchat.data.storage

import android.os.Environment
import com.talent.boomchat.data.model.FileCategory
import com.talent.boomchat.data.model.FileItem
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object StorageManager {

    val rootStorageDirectory: File
        get() = Environment.getExternalStorageDirectory()

    fun listFiles(directory: File, sortBy: SortOption = SortOption.NAME_ASC): List<FileItem> {
        val files = directory.listFiles() ?: return emptyList()
        val items = files.map { FileItem.fromFile(it) }

        return when (sortBy) {
            SortOption.NAME_ASC -> items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            SortOption.NAME_DESC -> items.sortedWith(compareBy({ !it.isDirectory }, { -it.name.lowercase().hashCode() }))
            SortOption.DATE_DESC -> items.sortedWith(compareBy({ !it.isDirectory }, { -it.lastModified }))
            SortOption.DATE_ASC -> items.sortedWith(compareBy({ !it.isDirectory }, { it.lastModified }))
            SortOption.SIZE_DESC -> items.sortedWith(compareBy({ !it.isDirectory }, { -it.sizeBytes }))
        }
    }

    fun searchFiles(directory: File, query: String): List<FileItem> {
        val results = mutableListOf<FileItem>()
        fun searchRecursive(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (file.name.contains(query, ignoreCase = true)) {
                    results.add(FileItem.fromFile(file))
                }
                if (file.isDirectory && !file.name.startsWith(".")) {
                    searchRecursive(file)
                }
            }
        }
        searchRecursive(directory)
        return results
    }

    fun deleteFile(file: File): Boolean {
        return if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }

    fun renameFile(source: File, newName: String): Boolean {
        val destination = File(source.parentFile, newName)
        return source.renameTo(destination)
    }

    fun copyFile(source: File, destinationDir: File): Boolean {
        return try {
            val destFile = File(destinationDir, source.name)
            if (source.isDirectory) {
                source.copyRecursively(destFile, overwrite = true)
            } else {
                FileInputStream(source).use { inStream ->
                    FileOutputStream(destFile).use { outStream ->
                        inStream.copyTo(outStream)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun moveFile(source: File, destinationDir: File): Boolean {
        val copied = copyFile(source, destinationDir)
        if (copied) {
            deleteFile(source)
            return true
        }
        return false
    }

    fun createFolder(parentDir: File, folderName: String): Boolean {
        val newFolder = File(parentDir, folderName)
        return if (!newFolder.exists()) newFolder.mkdirs() else false
    }

    enum class SortOption {
        NAME_ASC,
        NAME_DESC,
        DATE_DESC,
        DATE_ASC,
        SIZE_DESC
    }
}
