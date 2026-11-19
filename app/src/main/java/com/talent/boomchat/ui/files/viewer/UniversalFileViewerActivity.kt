package com.talent.boomchat.ui.files.viewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.talent.boomchat.data.model.FileCategory
import com.talent.boomchat.data.model.FileItem
import com.talent.boomchat.databinding.ActivityUniversalFileViewerBinding
import java.io.File
import java.io.FileInputStream
import java.util.Scanner

class UniversalFileViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversalFileViewerBinding
    private var targetFile: File? = null

    // PDF variables
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var pfd: ParcelFileDescriptor? = null
    private var pdfPageIndex = 0

    // Audio/Video player
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val audioProgressRunnable = object : Runnable {
        override fun run() {
            player?.let { p ->
                if (p.isPlaying) {
                    val current = p.currentPosition.toInt()
                    binding.sbAudioProgress.progress = current
                    binding.tvAudioCurrentTime.text = formatTime(current)
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversalFileViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = intent.getStringExtra(EXTRA_FILE_PATH)
        targetFile = if (path != null) {
            File(path)
        } else if (intent.data != null) {
            File(intent.data!!.path ?: "")
        } else null

        if (targetFile == null || !targetFile!!.exists()) {
            Toast.makeText(this, "File not found or cannot be opened", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.viewerToolbar.title = targetFile!!.name
        binding.viewerToolbar.setNavigationOnClickListener { finish() }

        renderFile(targetFile!!)
    }

    private fun renderFile(file: File) {
        val fileItem = FileItem.fromFile(file)
        binding.viewerToolbar.subtitle = "${fileItem.category.name} • ${fileItem.sizeBytes / 1024} KB"

        when (fileItem.category) {
            FileCategory.PDF -> showPdf(file)
            FileCategory.IMAGE -> showImage(file)
            FileCategory.VIDEO -> showVideo(file)
            FileCategory.AUDIO -> showAudio(file)
            FileCategory.WORD, FileCategory.EXCEL, FileCategory.POWERPOINT -> showOfficeDocument(file, fileItem.category)
            FileCategory.TEXT_CODE, FileCategory.ARCHIVE, FileCategory.UNKNOWN, FileCategory.DIRECTORY -> showTextOrCode(file)
        }
    }

    private fun showPdf(file: File) {
        binding.containerPdf.visibility = View.VISIBLE
        try {
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(pfd!!)
            renderPdfPage(0)

            binding.btnPdfPrev.setOnClickListener {
                if (pdfPageIndex > 0) {
                    renderPdfPage(pdfPageIndex - 1)
                }
            }

            binding.btnPdfNext.setOnClickListener {
                if (pdfPageIndex < (pdfRenderer?.pageCount ?: 1) - 1) {
                    renderPdfPage(pdfPageIndex + 1)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to render PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            showTextOrCode(file)
        }
    }

    private fun renderPdfPage(index: Int) {
        val renderer = pdfRenderer ?: return
        currentPage?.close()
        currentPage = renderer.openPage(index)
        pdfPageIndex = index

        val bitmap = Bitmap.createBitmap(
            currentPage!!.width,
            currentPage!!.height,
            Bitmap.Config.ARGB_8888
        )
        currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        binding.ivPdfPage.setImageBitmap(bitmap)
        binding.tvPdfPageIndicator.text = "Page ${index + 1} of ${renderer.pageCount}"
    }

    private fun showImage(file: File) {
        binding.containerImage.visibility = View.VISIBLE
        Glide.with(this).load(file).into(binding.containerImage)
    }

    private fun showVideo(file: File) {
        binding.containerVideo.visibility = View.VISIBLE
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
            prepare()
            playWhenReady = true
        }
        binding.containerVideo.player = player
    }

    private fun showAudio(file: File) {
        binding.containerAudio.visibility = View.VISIBLE
        binding.tvAudioFileName.text = file.name

        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
            prepare()
        }

        binding.fabAudioPlayPause.setOnClickListener {
            player?.let { p ->
                if (p.isPlaying) {
                    p.pause()
                    binding.fabAudioPlayPause.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    p.play()
                    binding.fabAudioPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                    binding.sbAudioProgress.max = p.duration.toInt()
                    binding.tvAudioTotalTime.text = formatTime(p.duration.toInt())
                    handler.post(audioProgressRunnable)
                }
            }
        }

        binding.sbAudioProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) player?.seekTo(progress.toLong())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showOfficeDocument(file: File, category: FileCategory) {
        binding.containerOfficeDoc.visibility = View.VISIBLE
        binding.tvDocBadge.text = category.name.replace("_", " ")

        // Extract and display document content
        val content = try {
            val sb = StringBuilder()
            val scanner = Scanner(FileInputStream(file))
            var lines = 0
            while (scanner.hasNextLine() && lines < 500) {
                sb.append(scanner.nextLine()).append("\n")
                lines++
            }
            scanner.close()
            sb.toString()
        } catch (e: Exception) {
            "Unable to parse document binary directly. File path: ${file.absolutePath}\nSize: ${file.length()} bytes"
        }

        binding.tvDocContent.text = content.ifEmpty { "Empty or protected document" }
    }

    private fun showTextOrCode(file: File) {
        binding.containerTextCode.visibility = View.VISIBLE
        try {
            val sb = StringBuilder()
            file.forEachLine { line ->
                sb.append(line).append("\n")
            }
            binding.tvCodeContent.text = sb.toString()
        } catch (e: Exception) {
            binding.tvCodeContent.text = "File Content (Binary/Hex):\n${file.name} (${file.length()} bytes)"
        }
    }

    private fun formatTime(millis: Int): String {
        val totalSecs = millis / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return String.format("%02d:%02d", mins, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
        pfd?.close()
        player?.release()
        player = null
        handler.removeCallbacks(audioProgressRunnable)
    }

    companion object {
        const val EXTRA_FILE_PATH = "extra_file_path"
    }
}
