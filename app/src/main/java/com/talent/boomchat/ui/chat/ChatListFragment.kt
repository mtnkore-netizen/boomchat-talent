package com.talent.boomchat.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewChat.setOnClickListener {
            showNewChatDialog()
        }
    }

    private fun showNewChatDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter recipient user's email or UID"
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Start New Chat")
            .setMessage("Connect with a fellow believer on BoomChat Talent:")
            .setView(input)
            .setPositiveButton("Open Chat") { _, _ ->
                val recipient = input.text.toString().trim()
                if (recipient.isNotEmpty()) {
                    val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
                        putExtra(ChatRoomActivity.EXTRA_RECIPIENT_ID, recipient)
                        putExtra(ChatRoomActivity.EXTRA_RECIPIENT_NAME, recipient)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Recipient cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
