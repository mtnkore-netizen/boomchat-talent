package com.talent.boomchat.ui.spiritual

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.talent.boomchat.R
import com.talent.boomchat.databinding.FragmentSpiritualHubBinding
import com.talent.boomchat.ui.spiritual.bible.AudioBibleActivity
import com.talent.boomchat.ui.spiritual.devotional.DailyDevotionalActivity
import com.talent.boomchat.ui.spiritual.godlyvideos.GodlyTikTokActivity
import com.talent.boomchat.ui.spiritual.lectio.LectioDivinaActivity
import com.talent.boomchat.ui.spiritual.livestudy.LiveBibleStudyActivity
import com.talent.boomchat.ui.spiritual.music.ChristianMusicActivity
import com.talent.boomchat.ui.spiritual.prayer.GuidedPrayerActivity
import com.talent.boomchat.ui.spiritual.scripture.GuidedScriptureActivity

class SpiritualHubFragment : Fragment() {

    private var _binding: FragmentSpiritualHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpiritualHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCards()
    }

    private fun setupCards() {
        // 1. Dramatized Audio Bible
        binding.cardAudioBible.apply {
            tvCardTitle.text = "Dramatized Audio Bible"
            tvCardDesc.text = "All 66 Books with synchronized text highlighting & audio narration"
            ivCardIcon.setImageResource(android.R.drawable.ic_menu_compass)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), AudioBibleActivity::class.java))
            }
        }

        // 2. Lectio Divina / Devote
        binding.cardLectioDivina.apply {
            tvCardTitle.text = "Lectio Divina / Pray As You Go"
            tvCardDesc.text = "4-stage meditative prayer: Read, Meditate, Pray, Contemplate"
            ivCardIcon.setImageResource(android.R.drawable.ic_menu_edit)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), LectioDivinaActivity::class.java))
            }
        }

        // 3. Daily Guided Prayer
        binding.cardGuidedPrayer.apply {
            tvCardTitle.text = "Daily Guided Audio Prayer"
            tvCardDesc.text = "Listen to guided prayers with matching prayer text & upload portal"
            ivCardIcon.setImageResource(android.R.drawable.ic_btn_speak_now)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), GuidedPrayerActivity::class.java))
            }
        }

        // 4. Daily Guided Audio Scripture
        binding.cardGuidedScripture.apply {
            tvCardTitle.text = "Daily Guided Audio Scripture"
            tvCardDesc.text = "Audio scripture readings accompanied by Biblical reflection text"
            ivCardIcon.setImageResource(android.R.drawable.ic_menu_agenda)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), GuidedScriptureActivity::class.java))
            }
        }

        // 5. Godly TikTok Short Videos
        binding.cardGodlyTikTok.apply {
            tvCardTitle.text = "Godly Short Videos (TikTok Style)"
            tvCardDesc.text = "Full-screen vertical feed of Godly videos with upload & likes"
            ivCardIcon.setImageResource(android.R.drawable.ic_media_play)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), GodlyTikTokActivity::class.java))
            }
        }

        // 6. Christian Music (Audio Only)
        binding.cardChristianMusic.apply {
            tvCardTitle.text = "Christian Music Hub"
            tvCardDesc.text = "Pure Christian worship & praise audio streaming with music upload"
            ivCardIcon.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), ChristianMusicActivity::class.java))
            }
        }

        // 7. Live Bible Study Room
        binding.cardLiveBibleStudy.apply {
            tvCardTitle.text = "Live Bible Study Room"
            tvCardDesc.text = "Teaching the Bible in bits alongside text with live questions & notes"
            ivCardIcon.setImageResource(android.R.drawable.ic_menu_myplaces)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), LiveBibleStudyActivity::class.java))
            }
        }

        // 8. Guided Daily Devotional
        binding.cardDailyDevotional.apply {
            tvCardTitle.text = "Guided Daily Devotionals"
            tvCardDesc.text = "Daily spiritual reflections, prayer points & devotion upload portal"
            ivCardIcon.setImageResource(android.R.drawable.ic_menu_today)
            root.setOnClickListener {
                startActivity(Intent(requireContext(), DailyDevotionalActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
