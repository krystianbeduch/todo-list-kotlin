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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.krybed.todolist.R
import com.krybed.todolist.data.model.Task
import com.krybed.todolist.data.model.Attachment
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.data.model.enums.SortType
import com.krybed.todolist.databinding.FragmentHomeBinding
import com.krybed.todolist.presentation.activity.TaskActivity
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.util.file.FileService
import com.krybed.todolist.util.lang.LocalHelper
import com.krybed.todolist.util.notification.NotificationUtils
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val b get() = binding!!

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    private var currentAttachmentTask: Task? = null

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
                    Log.i("Attachment", "Selected file: $localUri")
                    val attachment = Attachment(
                        taskId = currentAttachmentTask!!.id,
                        filename = filename,
                        filePath = localUri.toString()
                    )
                    taskViewModel.addAttachmentToTask(attachment)
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        taskViewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]
        NotificationUtils.createNotificationChannel(requireContext())
        initRecyclerView()

        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val safeTasks = tasks ?: emptyList()
            taskAdapter.updateTasks(safeTasks)

            taskViewModel.hasInsertedDummy.observe(viewLifecycleOwner) { hasInserted ->
                if ((hasInserted == null || !hasInserted) && safeTasks.isEmpty()) {
                    insertDummyTasks()
                    taskViewModel.markDummyInserted()
                }
            }

            taskViewModel.notificationChecked.observe(viewLifecycleOwner) { checked ->
                if (checked != null) {
                    checkForUpcomingDeadlines(safeTasks, checked)
                    if (!checked) {
                        taskViewModel.markNotificationChecked()
                    }
                }
            }
        }
        return b.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

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
                            val currentTasks = taskViewModel.tasks.value
                            if (currentTasks != null) {
                                taskViewModel.sortTasks(currentTasks.toMutableList(), selected)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                    }
                }
                val langItem = menu.findItem(R.id.current_lang_flag)
                when (Locale.getDefault().language) {
                    "pl" -> langItem.setIcon(R.drawable.ic_polish)
                    "en" -> langItem.setIcon(R.drawable.ic_english)
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
//        b.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
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
                                deleteAttachment(task, selected)
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

    private fun deleteAttachment(task: Task, attachment: Attachment) {
        if (FileService.deleteFileFromInternalStorage(
                requireContext(), attachment.filePath
        )) {
            taskViewModel.deleteAttachment(attachment)
            task.attachments.removeIf { it.id == attachment.id }
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
        isNotificationShown: Boolean
    ) {
        val now = LocalDateTime.now()
        val threshold = now.plusHours(24)
        val tasksToNotify = mutableListOf<Task>()

        for (task in tasks) {
            if (!task.isDone) {
                if (task.deadline.isBefore(now)) {
                    task.notificationType = NotificationType.OVERDUE
                    tasksToNotify.add(task)
                    if (!isNotificationShown) {
                        NotificationUtils.showTaskNotification(requireContext(), task)
                    }
                }
                else if (task.deadline.isAfter(now) &&
                    task.deadline.isBefore(threshold)
                ) {
                    task.notificationType = NotificationType.UPCOMING
                    tasksToNotify.add(task)
                    if (!isNotificationShown) {
                        NotificationUtils.showTaskNotification(requireContext(), task)
                    }
                }
            }
        }
        taskViewModel.updateTasksForNotification(tasksToNotify)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}