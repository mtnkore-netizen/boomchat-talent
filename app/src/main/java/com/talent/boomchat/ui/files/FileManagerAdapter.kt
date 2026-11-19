package com.talent.boomchat.ui.files

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.talent.boomchat.R
import com.talent.boomchat.data.model.FileCategory
import com.talent.boomchat.data.model.FileItem
import com.talent.boomchat.databinding.ItemFileBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManagerAdapter(
    private var items: List<FileItem>,
    private val onItemClick: (FileItem) -> Unit,
    private val onOptionsClick: (FileItem, String) -> Unit
) : RecyclerView.Adapter<FileManagerAdapter.FileViewHolder>() {

    fun updateList(newItems: List<FileItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class FileViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileItem) {
            binding.tvFileName.text = item.name

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date(item.lastModified))

            if (item.isDirectory) {
                binding.tvFileDetails.text = "${item.childCount} items • $dateStr"
                binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_agenda)
            } else {
                val sizeKb = item.sizeBytes / 1024
                val sizeText = if (sizeKb > 1024) "${sizeKb / 1024} MB" else "$sizeKb KB"
                binding.tvFileDetails.text = "$sizeText • $dateStr"

                when (item.category) {
                    FileCategory.PDF -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_report_image)
                    FileCategory.WORD, FileCategory.EXCEL, FileCategory.POWERPOINT -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size)
                    FileCategory.IMAGE -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_gallery)
                    FileCategory.VIDEO -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_media_play)
                    FileCategory.AUDIO -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
                    FileCategory.TEXT_CODE -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_edit)
                    else -> binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_save)
                }
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.btnFileOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menu.add("Open")
                popup.menu.add("Share to BoomChat")
                popup.menu.add("Rename")
                popup.menu.add("Delete")
                popup.setOnMenuItemClickListener { menuItem ->
                    onOptionsClick(item, menuItem.title.toString())
                    true
                }
                popup.show()
            }
        }
    }
}
