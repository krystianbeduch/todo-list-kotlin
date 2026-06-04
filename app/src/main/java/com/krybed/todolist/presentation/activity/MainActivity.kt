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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.krybed.todolist.R
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.mapper.AttachmentMapper
import com.krybed.todolist.data.mapper.TaskMapper
import com.krybed.todolist.data.model.enums.FileType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.data.repository.AttachmentRepositoryImpl
import com.krybed.todolist.data.repository.TaskRepositoryImpl
import com.krybed.todolist.databinding.ActivityMainBinding
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.presentation.AppContainer
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.presentation.viewmodel.TaskViewModelFactory
import com.krybed.todolist.util.file.FileService
import com.krybed.todolist.util.lang.LocalHelper
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentImportFileType: FileType? = null
    private val taskViewModel: TaskViewModel by viewModels {
        AppContainer.provideTaskViewModelFactory(applicationContext)
    }
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

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment

        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            appBarConfiguration
        )
        NavigationUI.setupWithNavController(navView, navController)

        // SAMPLE DATA
        seedDummyTasksIfNeeded()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.tasksForNotification.collect { tasks ->
//                    val badge = navView.getOrCreateBadge(R.id.navigation_notifications)
//                    if (tasks.isNotEmpty()) {
//                        badge.isVisible = true
//                        badge.number = tasks.size
//                    }
//                    else {
//                        badge.isVisible = false
//                    }

                    navView.getOrCreateBadge(R.id.navigation_notifications).apply {
                        isVisible = tasks.isNotEmpty()
                        if (isVisible) number = tasks.size
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
                R.id.menu_delete_all -> {
                    showDeleteAllDialog()
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
                getString(R.string.select_file, fileType.name)
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

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_all_title))
            .setMessage(getString(R.string.delete_all_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                taskViewModel.deleteAll()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun seedDummyTasksIfNeeded() {
        lifecycleScope.launch {
            val tasks = taskViewModel.getAllOnce()
            if (tasks.isEmpty()) {
                insertDummyTasks()
            }
        }
    }

    private fun insertDummyTasks() {
        taskViewModel.insert(
            Task.create(
                "Do the shopping",
                LocalDateTime.now().plusDays(5).with(LocalTime.of(12, 0)),
                true,
                Priority.HIGH
            )
        )
        taskViewModel.insert(
            Task.create(
                "Gym session",
                LocalDateTime.now().plusDays(3).with(LocalTime.of(14, 30)),
                false,
                Priority.MEDIUM
            )
        )
        taskViewModel.insert(
            Task.create(
                "Team meeting",
                LocalDateTime.now().plusDays(15).with(LocalTime.of(7, 25)),
                false,
                Priority.MEDIUM
            )
        )
        taskViewModel.insert(
            Task.create(
                "Dentist appointment",
                LocalDateTime.now().plusDays(25).with(LocalTime.of(9, 50)),
                false,
                Priority.LOW
            )
        )
        taskViewModel.insert(
            Task.create(
                "Project deadline",
                LocalDateTime.now().plusDays(2).with(LocalTime.of(18, 0)),
                true,
                Priority.LOW
            )
        )
        taskViewModel.insert(
            Task.create(
                "Finish reading the book",
                LocalDateTime.now().with(LocalTime.of(23, 59)),
                false,
                Priority.HIGH
            )
        )
        taskViewModel.insert(
            Task.create(
                "Water the plants",
                LocalDateTime.now().minusDays(3),
                false,
                Priority.HIGH
            )
        )
    }
}