package com.krybed.todolist.presentation.home;

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.krybed.todolist.R
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.SortType
import com.krybed.todolist.databinding.FragmentHomeBinding
import com.krybed.todolist.domain.model.Attachment
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.presentation.AppContainer
import com.krybed.todolist.presentation.activity.TaskActivity
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.util.file.FileService
import com.krybed.todolist.util.lang.LocalHelper
import com.krybed.todolist.util.notification.NotificationUtils
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val b get() = binding!!
    private lateinit var taskAdapter: TaskAdapter
    private var currentAttachmentTask: Task? = null
    private val shownNotificationKeys = mutableSetOf<String>()
    private val taskViewModel: TaskViewModel by activityViewModels {
        AppContainer.provideTaskViewModelFactory(requireContext().applicationContext)
    }

    private val attachmentPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val sourceUri = result.data?.data ?: return@registerForActivityResult
                val filename = FileService.getFilenameFromUri(
                    requireContext(),
                    sourceUri
                ) ?: return@registerForActivityResult
                val localUri = FileService.copyFileToInternalStorage(
                    requireContext(), sourceUri, filename
                )

                if (localUri != null && currentAttachmentTask != null) {
                    val task = currentAttachmentTask ?: return@registerForActivityResult

                    val attachment = Attachment(
                        taskId = task.id,
                        filename = filename,
                        filePath = localUri.toString()
                    )

                    taskViewModel.addAttachmentToTask(attachment)

                    Log.i("Attachment", "Selected file: $localUri")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.attachment_added),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        NotificationUtils.createNotificationChannel(requireContext())
        initRecyclerView()
        return b.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupMenu()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.tasks.collect { tasks ->
                    taskAdapter.updateTasks(tasks)
                    checkForUpcomingDeadlines(tasks)
                }
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.sort_menu, menu)

                val item = menu.findItem(R.id.sort_spinner_item)
                val spinner = item.actionView as? Spinner
                spinner?.let {
                    val sortDisplayNames = SortType.entries.map { type ->
                        getString(type.stringResId)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        sortDisplayNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    it.adapter = adapter
                    it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedName = parent?.getItemAtPosition(position) as String
                            val selected = SortType.fromDisplayName(
                                requireContext(),
                                selectedName
                            )
                            taskViewModel.loadTasksBySort(selected)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                    }
                }
                val langItem = menu.findItem(R.id.current_lang_flag)
                when (Locale.getDefault().language) {
                    "en" -> langItem.setIcon(R.drawable.ic_english)
                    "pl" -> langItem.setIcon(R.drawable.ic_polish)
                    "de" -> langItem.setIcon(R.drawable.ic_germany)
                    "es" -> langItem.setIcon(R.drawable.ic_spain)
                    "fr" -> langItem.setIcon(R.drawable.ic_france)
                    "it" -> langItem.setIcon(R.drawable.ic_italy)
                    "pt" -> langItem.setIcon(R.drawable.ic_portugal)
                    "tr" -> langItem.setIcon(R.drawable.ic_turkey)
                    "uk" -> langItem.setIcon(R.drawable.ic_ukraine)
                    "ar" -> langItem.setIcon(R.drawable.ic_saudi_arabia)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.current_lang_flag -> {
                        LocalHelper.showChangeLanguageDialog(
                            requireContext()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun initRecyclerView() {
        b.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(emptyList(), object : TaskAdapter.OnTaskClickListener {

            override fun onEditClick(task: Task) {
                Log.i("Task", "Edit: ${task.id}. ${task.title}")
                startActivity(
                    Intent(context, TaskActivity::class.java)
                        .putExtra("taskId", task.id)
                )
            }

            override fun onDeleteClick(task: Task) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setMessage(getString(
                        R.string.confirm_delete_task_message) + "\"${task.title}\"?"
                    )
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        Log.i("Delete task", "${task.id}. ${task.title}")
                        taskViewModel.delete(task)
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                    }
                    .show()
            }

            override fun onChangeStatusClick(task: Task) {
                Log.i("Change task status", "${task.id}.${task.title}")
                taskViewModel.changeStatus(task)
            }

            override fun onLongClick(task: Task) {
                onChangeStatusClick(task)
            }

            override fun onAddAttachmentClick(task: Task) {
                Log.i("Add attachment", "${task.id}.${task.title}")
                currentAttachmentTask = task

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/*", "video/*", "application/pdf")
                    )
                }
                attachmentPickerLauncher.launch(intent)
            }

            override fun onDeleteAttachmentClick(task: Task) {
                if (task.attachments.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_attachments_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val attachmentsName = task.attachments.map { it.filename }.toTypedArray()

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_attachment_alertdialog_title))
                    .setItems(attachmentsName) { dialog, which ->
                        val selected = task.attachments[which]

                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.confirm_delete_title))
                            .setMessage(getString(R.string.delete_attachment_alertdialog_message))
                            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                Log.i("Delete attachment", selected.filename)
                                deleteAttachment(selected)
                            }
                            .setNegativeButton(
                                getString(R.string.no)) { _, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    .setNegativeButton(
                        getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            override fun onShowAttachmentClick(task: Task) {
                if (task.attachments.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_attachments_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val attachmentsName = task.attachments.map { it.filename }.toTypedArray()

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.select_attachment))
                    .setItems(attachmentsName) { _, which ->
                            val selected = task.attachments[which]
                            openAttachment(requireContext(), selected)
                    }
                    .setNegativeButton(
                        getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        b.tasksRecyclerView.adapter = taskAdapter
    }
    private fun openAttachment(ctx: Context, attachment: Attachment) {
        try {
            val uri = attachment.filePath.toUri()
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, ctx.contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            ctx.startActivity(intent)
        }
        catch (_: ActivityNotFoundException) {
            Toast.makeText(
                ctx,
                ctx.getString(R.string.no_app_to_open_file),
                Toast.LENGTH_SHORT
            ).show()
        }
        catch (e: Exception) {
            Log.e("Open attachment error", Log.getStackTraceString(e))
        }
    }

    private fun deleteAttachment(attachment: Attachment) {
        if (FileService.deleteFileFromInternalStorage(
                requireContext(), attachment.filePath
        )) {
            taskViewModel.deleteAttachment(attachment)
            Toast.makeText(
                requireContext(),
                getString(R.string.attachment_deleted),
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.error_deleting_attachment),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkForUpcomingDeadlines(
        tasks: List<Task>,
    ) {
        val now = LocalDateTime.now()
        val threshold = now.plusHours(24)

        val tasksToNotify = mutableListOf<Task>()

        tasks.forEach { task ->
            if (task.isDone) return@forEach

            val notificationType = when {
                task.deadline.isBefore(now) -> NotificationType.OVERDUE
                task.deadline.isAfter(now) && task.deadline.isBefore(threshold) ->
                    NotificationType.UPCOMING
                else -> null
            } ?: return@forEach

            val notifiedTask = task.copy(notificationType = notificationType)
            tasksToNotify.add(notifiedTask)

            val key = "${task.id}_${notificationType.name}"
            if (!shownNotificationKeys.add(key)) return@forEach

            NotificationUtils.showTaskNotification(
                requireContext(),
                task.copy(notificationType = notificationType)
            )
        }

        taskViewModel.updateTasksForNotification(tasksToNotify)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}