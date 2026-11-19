package com.talent.boomchat.ui.files

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.talent.boomchat.data.model.FileItem
import com.talent.boomchat.data.storage.StorageManager
import com.talent.boomchat.databinding.FragmentFileManagerBinding
import com.talent.boomchat.ui.files.viewer.UniversalFileViewerActivity
import com.talent.boomchat.util.PermissionsHelper
import java.io.File

class FileManagerFragment : Fragment() {

    private var _binding: FragmentFileManagerBinding? = null
    private val binding get() = _binding!!

    private var currentDirectory: File = StorageManager.rootStorageDirectory
    private lateinit var adapter: FileManagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        loadDirectory(currentDirectory)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
        loadDirectory(currentDirectory)
    }

    private fun checkPermissions() {
        if (!PermissionsHelper.hasStorageManagerPermission()) {
            binding.layoutPermissionBanner.visibility = View.VISIBLE
        } else {
            binding.layoutPermissionBanner.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = FileManagerAdapter(
            items = emptyList(),
            onItemClick = { item ->
                if (item.isDirectory) {
                    loadDirectory(File(item.path))
                } else {
                    openFileInUniversalViewer(item.path)
                }
            },
            onOptionsClick = { item, action ->
                handleFileAction(item, action)
            }
        )
        binding.rvFiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFiles.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnGrantStoragePermission.setOnClickListener {
            PermissionsHelper.requestManageStoragePermission(requireActivity())
        }

        binding.btnNavBack.setOnClickListener {
            val parent = currentDirectory.parentFile
            if (parent != null && parent.canRead()) {
                loadDirectory(parent)
            } else {
                Toast.makeText(requireContext(), "Reached storage root", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }

        binding.btnNewFolder.setOnClickListener {
            showNewFolderDialog()
        }
    }

    private fun loadDirectory(dir: File) {
        currentDirectory = dir
        binding.tvCurrentPath.text = dir.absolutePath
        val fileItems = StorageManager.listFiles(dir)
        adapter.updateList(fileItems)
    }

    private fun openFileInUniversalViewer(filePath: String) {
        val intent = Intent(requireContext(), UniversalFileViewerActivity::class.java).apply {
            putExtra(UniversalFileViewerActivity.EXTRA_FILE_PATH, filePath)
        }
        startActivity(intent)
    }

    private fun handleFileAction(item: FileItem, action: String) {
        val file = File(item.path)
        when (action) {
            "Open" -> openFileInUniversalViewer(item.path)
            "Delete" -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete ${item.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        if (StorageManager.deleteFile(file)) {
                            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                            loadDirectory(currentDirectory)
                        } else {
                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            "Rename" -> {
                val input = EditText(requireContext()).apply { setText(item.name) }
                AlertDialog.Builder(requireContext())
                    .setTitle("Rename")
                    .setView(input)
                    .setPositiveButton("Save") { _, _ ->
                        val newName = input.text.toString().trim()
                        if (newName.isNotEmpty() && StorageManager.renameFile(file, newName)) {
                            Toast.makeText(requireContext(), "Renamed", Toast.LENGTH_SHORT).show()
                            loadDirectory(currentDirectory)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            "Share to BoomChat" -> {
                Toast.makeText(requireContext(), "Sharing ${item.name} to BoomChat…", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Name (A-Z)", "Name (Z-A)", "Newest First", "Oldest First", "Largest First")
        AlertDialog.Builder(requireContext())
            .setTitle("Sort Files By")
            .setItems(options) { _, which ->
                val sortOption = when (which) {
                    0 -> StorageManager.SortOption.NAME_ASC
                    1 -> StorageManager.SortOption.NAME_DESC
                    2 -> StorageManager.SortOption.DATE_DESC
                    3 -> StorageManager.SortOption.DATE_ASC
                    else -> StorageManager.SortOption.SIZE_DESC
                }
                val items = StorageManager.listFiles(currentDirectory, sortOption)
                adapter.updateList(items)
            }
            .show()
    }

    private fun showNewFolderDialog() {
        val input = EditText(requireContext()).apply { hint = "Folder name" }
        AlertDialog.Builder(requireContext())
            .setTitle("Create New Folder")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty() && StorageManager.createFolder(currentDirectory, name)) {
                    loadDirectory(currentDirectory)
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
