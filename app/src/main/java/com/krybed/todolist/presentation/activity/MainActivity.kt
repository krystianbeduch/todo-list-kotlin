package com.krybed.todolist.presentation.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.krybed.todolist.R
import com.krybed.todolist.data.model.enums.FileType
import com.krybed.todolist.databinding.ActivityMainBinding
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.util.file.FileService
import com.krybed.todolist.util.lang.LocalHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private var currentImportFileType: FileType? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val fileUri = result.data?.data
            if (fileUri != null) {
                val selectedType = currentImportFileType
                    ?: return@registerForActivityResult
                val filename = FileService.getFilenameFromFilePicker(this, fileUri)

                if (FileService.isSupportedExtensionForFileType(selectedType, filename))  {
                    taskViewModel.importTasksFromFile(this, fileUri, selectedType)
                }
                else {
                    Toast.makeText(
                        this,
                        getString(R.string.incorrect_file_format),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermission()

        val navView = binding.navView
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_task_manager,
            R.id.navigation_notifications
        ).build()

        val navController = this.findNavController(
            R.id.nav_host_fragment_activity_main
        )
        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            appBarConfiguration
        )
        NavigationUI.setupWithNavController(navView, navController)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

//        taskViewModel.tasksForNotification.observe(this) { tasks ->
//            val badge = navView.getOrCreateBadge(R.id.navigation_notifications)
//            if (tasks.isNotEmpty()) {
//                badge.isVisible = true
//                badge.number = tasks.size
//            }
//            else {
//                badge.isVisible = false
//            }
//        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.tasksForNotification.collect { tasks ->
                    val badge = navView.getOrCreateBadge(R.id.navigation_notifications)
                    if (tasks.isNotEmpty()) {
                        badge.isVisible = true
                        badge.number = tasks.size
                    }
                    else {
                        badge.isVisible = false
                    }
                }
            }
        }

        navView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_more) {
                val moreItemView = findViewById<View>(R.id.nav_more)
                if (moreItemView != null) {
                    showMorePopup(moreItemView)
                }
                true
            }
            else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalHelper.applySavedLocale(newBase))
    }

    private fun showMorePopup(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.bottom_nav_more, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_import -> {
                    showFormatChooser(true)
                    true
                }
                R.id.menu_export -> {
                    showFormatChooser(false)
                    true
                }
                R.id.change_language -> {
                    LocalHelper.showChangeLanguageDialog(this)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showFormatChooser(isImport: Boolean) {
        val formats = FileType.entries.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_file_format))
            .setItems(formats) { _, which ->
                val selectedFormat = FileType.entries[which]
                if (isImport) {
                    currentImportFileType = selectedFormat
                    openFilePicker(selectedFormat)
                }
                else {
                    exportTasks(selectedFormat)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openFilePicker(fileType: FileType) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = fileType.mimeType
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        filePickerLauncher.launch(
            Intent.createChooser(
                intent,
                getString(R.string.select_file) + fileType.name
            )
        )
    }

    private fun exportTasks(fileType: FileType) =
        taskViewModel.exportTasksToFile(this, fileType)

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}