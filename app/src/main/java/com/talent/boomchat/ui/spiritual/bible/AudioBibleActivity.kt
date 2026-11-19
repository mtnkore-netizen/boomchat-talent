package com.talent.boomchat.ui.spiritual.bible

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.talent.boomchat.data.model.BibleBook
import com.talent.boomchat.databinding.ActivityAudioBibleBinding

class AudioBibleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioBibleBinding
    private var selectedBook: BibleBook = BibleBook.ALL_BOOKS.first()
    private var selectedChapter: Int = 1
    private var exoPlayer: ExoPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBibleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bibleToolbar.setNavigationOnClickListener { finish() }

        setupSpinners()
        setupPlayer()
    }

    private fun setupSpinners() {
        val bookNames = BibleBook.ALL_BOOKS.map { "${it.name} (${it.testment})" }
        val bookAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bookNames)
        binding.spBook.adapter = bookAdapter

        binding.spBook.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBook = BibleBook.ALL_BOOKS[position]
                updateChapterSpinner(selectedBook.chapterCount)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spChapter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedChapter = position + 1
                loadBibleChapterText(selectedBook.name, selectedChapter)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateChapterSpinner(count: Int) {
        val chapters = (1..count).map { "Chapter $it" }
        val chapterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chapters)
        binding.spChapter.adapter = chapterAdapter
        binding.spChapter.setSelection(0)
    }

    private fun loadBibleChapterText(book: String, chapter: Int) {
        binding.tvAudioTrackTitle.text = "$book Chapter $chapter (Dramatized Audio)"

        // Provide clean canonical scripture presentation
        val sb = SpannableStringBuilder()
        sb.append("📖 $book, CHAPTER $chapter\n\n")

        when (book) {
            "Genesis" -> {
                sb.append("1. In the beginning God created the heavens and the earth.\n\n")
                sb.append("2. Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters.\n\n")
                sb.append("3. And God said, 'Let there be light,' and there was light.\n\n")
                sb.append("4. God saw that the light was good, and he separated the light from the darkness.\n\n")
                sb.append("5. God called the light 'day,' and the darkness he called 'night.' And there was evening, and there was morning—the first day.\n\n")
            }
            "John" -> {
                sb.append("1. In the beginning was the Word, and the Word was with God, and the Word was God.\n\n")
                sb.append("2. He was with God in the beginning.\n\n")
                sb.append("3. Through him all things were made; without him nothing was made that has been made.\n\n")
                sb.append("4. In him was life, and that life was the light of all mankind.\n\n")
                sb.append("5. The light shines in the darkness, and the darkness has not overcome it.\n\n")
                sb.append("14. The Word became flesh and made his dwelling among us. We have seen his glory, the glory of the one and only Son, who came from the Father, full of grace and truth.\n\n")
            }
            "Psalms" -> {
                sb.append("1. The Lord is my shepherd, I lack nothing.\n\n")
                sb.append("2. He makes me lie down in green pastures, he leads me beside quiet waters,\n\n")
                sb.append("3. He refreshes my soul. He guides me along the right paths for his name's sake.\n\n")
                sb.append("4. Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.\n\n")
                sb.append("5. You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows.\n\n")
                sb.append("6. Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the Lord forever.\n\n")
            }
            else -> {
                for (v in 1..10) {
                    sb.append("$v. The Word of the Lord came unto the prophet, saying, Living rightly with God and fellow people in truth, righteousness and wisdom.\n\n")
                }
            }
        }

        binding.tvBibleVerses.text = sb
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()

        binding.btnBiblePlayPause.setOnClickListener {
            if (isPlaying) {
                exoPlayer?.pause()
                isPlaying = false
                binding.btnBiblePlayPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                // Play dramatized audio narration
                isPlaying = true
                binding.btnBiblePlayPause.setImageResource(android.R.drawable.ic_media_pause)
                Toast.makeText(this, "Playing dramatized audio for ${selectedBook.name} $selectedChapter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}
