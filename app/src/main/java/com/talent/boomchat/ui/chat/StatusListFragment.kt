package com.talent.boomchat.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.talent.boomchat.databinding.FragmentStatusListBinding

class StatusListFragment : Fragment() {

    private var _binding: FragmentStatusListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutMyStatus.setOnClickListener {
            openCreateStatus(isMedia = false)
        }

        binding.fabTextStatus.setOnClickListener {
            openCreateStatus(isMedia = false)
        }

        binding.fabMediaStatus.setOnClickListener {
            openCreateStatus(isMedia = true)
        }
    }

    private fun openCreateStatus(isMedia: Boolean) {
        val intent = Intent(requireContext(), CreateStatusActivity::class.java).apply {
            putExtra(CreateStatusActivity.EXTRA_IS_MEDIA, isMedia)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
