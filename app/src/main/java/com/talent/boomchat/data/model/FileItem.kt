package com.talent.boomchat.data.model

import java.io.File

enum class FileCategory {
    PDF,
    WORD,
    EXCEL,
    POWERPOINT,
    IMAGE,
    VIDEO,
    AUDIO,
    TEXT_CODE,
    ARCHIVE,
    DIRECTORY,
    UNKNOWN
}

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModified: Long,
    val mimeType: String,
    val category: FileCategory,
    val childCount: Int = 0
) {
    companion object {
        fun fromFile(file: File): FileItem {
            val isDir = file.isDirectory
            val name = file.name
            val ext = file.extension.lowercase()
            val category = when {
                isDir -> FileCategory.DIRECTORY
                ext == "pdf" -> FileCategory.PDF
                ext in listOf("doc", "docx", "rtf") -> FileCategory.WORD
                ext in listOf("xls", "xlsx", "csv") -> FileCategory.EXCEL
                ext in listOf("ppt", "pptx") -> FileCategory.POWERPOINT
                ext in listOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "svg") -> FileCategory.IMAGE
                ext in listOf("mp4", "mkv", "webm", "avi", "mov", "3gp", "flv") -> FileCategory.VIDEO
                ext in listOf("mp3", "wav", "aac", "m4a", "flac", "ogg", "wma") -> FileCategory.AUDIO
                ext in listOf("txt", "json", "xml", "html", "kt", "java", "py", "md", "log", "c", "cpp") -> FileCategory.TEXT_CODE
                ext in listOf("zip", "rar", "7z", "tar", "gz") -> FileCategory.ARCHIVE
                else -> FileCategory.UNKNOWN
            }
            return FileItem(
                name = name,
                path = file.absolutePath,
                isDirectory = isDir,
                sizeBytes = if (isDir) 0L else file.length(),
                lastModified = file.lastModified(),
                mimeType = when (category) {
                    FileCategory.PDF -> "application/pdf"
                    FileCategory.WORD -> "application/msword"
                    FileCategory.EXCEL -> "application/vnd.ms-excel"
                    FileCategory.POWERPOINT -> "application/vnd.ms-powerpoint"
                    FileCategory.IMAGE -> "image/*"
                    FileCategory.VIDEO -> "video/*"
                    FileCategory.AUDIO -> "audio/*"
                    FileCategory.TEXT_CODE -> "text/plain"
                    FileCategory.ARCHIVE -> "application/zip"
                    FileCategory.DIRECTORY -> "inode/directory"
                    FileCategory.UNKNOWN -> "*/*"
                },
                category = category,
                childCount = if (isDir) file.listFiles()?.size ?: 0 else 0
            )
        }
    }
}
