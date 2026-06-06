package com.krybed.todolist.presentation.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.krybed.todolist.R
import com.krybed.todolist.databinding.FragmentTaskFormBinding
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.presentation.AppContainer
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.util.converter.Converters
import com.krybed.todolist.util.lang.LocalHelper
import com.krybed.todolist.util.task.TaskFormHelper
import kotlinx.coroutines.launch

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: FragmentTaskFormBinding
    private var taskToEdit: Task? = null
    private val taskViewModel: TaskViewModel by viewModels {
        AppContainer.provideTaskViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId == -1) {
            Toast.makeText(
                this,
                getString(R.string.error_no_task_id),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        title = getString(R.string.edit_task_header)
        binding.taskFormHeader.text = getString(R.string.edit_task_header)

        val helper = TaskFormHelper(
            taskViewModel = taskViewModel,
            ctx = this,
            titleEditText = binding.taskTitle,
            deadlineEditText = binding.taskDeadline,
            prioritySpinner = binding.taskPriority
        )

        binding.taskSaveButton.setOnClickListener { _ ->
            helper.handleSave(
                callback = { finish() },
                isEditMode = true,
                existingTask = taskToEdit
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.getTaskById(taskId).collect { task ->
                    if (task == null) {
                        Toast.makeText(
                            this@TaskActivity,
                            getString(R.string.no_task_found),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@collect
                    }

                    taskToEdit = task
                    binding.taskTitle.setText(task.title)
                    binding.taskDeadline.setText(Converters.fromLocalDateTimeToString(task.deadline))
                    binding.taskPriority.setSelection(task.priority.value)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalHelper.applySavedLocale(newBase))
    }
}