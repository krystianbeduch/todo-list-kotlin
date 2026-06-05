package com.krybed.todolist.util.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.krybed.todolist.R
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import com.krybed.todolist.util.converter.Converters
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.Calendar

class TaskFormHelper (
    private val taskViewModel: TaskViewModel,
    private val ctx: Context,
    private val titleEditText: EditText,
    private val deadlineEditText: EditText,
    private val prioritySpinner: Spinner
) {

    private val selectedDateTime: Calendar = Calendar.getInstance()

    fun interface OnTaskSaveCallback {
        fun onSuccess(task: Task)
    }

    init {
        setupDeadlinePicker()
        setupPrioritySpinner()
    }

    fun handleSave(
        callback: OnTaskSaveCallback,
        isEditMode: Boolean,
        existingTask: Task?
    ) {
        val title = titleEditText.text.toString().trim()
        val deadlineText = deadlineEditText.text.toString().trim()
        val selectedPriority = prioritySpinner.selectedItem as Priority

        if (title.isEmpty() || deadlineText.isEmpty()) {
            Toast.makeText(
                ctx,
                ctx.getString(R.string.complete_all_fields),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val deadline = try {
            Converters.fromStringToLocalDateTime(deadlineText)
        } catch (_: DateTimeParseException) {
            Toast.makeText(
                ctx,
                ctx.getString(R.string.incorrect_date_format),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!deadline.isAfter(LocalDateTime.now())) {
            Toast.makeText(
                ctx,
                ctx.getString(R.string.incorrect_date),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (isEditMode && existingTask != null) {
            val updatedTask = existingTask.copy(
                title = title,
                deadline = deadline,
                priority = selectedPriority
            )

            taskViewModel.update(updatedTask)

            Toast.makeText(
                ctx,
                ctx.getString(R.string.task_updated),
                Toast.LENGTH_SHORT
            ).show()

            callback.onSuccess(updatedTask)
        }
        else {
            val newTask = Task(
                title = title,
                deadline = deadline,
                priority = selectedPriority
            )

            taskViewModel.insert(newTask)

            Toast.makeText(
                ctx,
                ctx.getString(R.string.task_added),
                Toast.LENGTH_SHORT
            ).show()

            callback.onSuccess(newTask)
        }
    }

    private fun setupDeadlinePicker() =
        deadlineEditText.setOnClickListener {
            showDateTimePicker()
        }

    private fun setupPrioritySpinner() {
        val adapter = object : ArrayAdapter<Priority>(
            ctx,
            android.R.layout.simple_spinner_item,
            Priority.entries.toTypedArray()
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View =
                bindPriorityText(
                    super.getView(position, convertView, parent),
                    position
                )

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View =
                bindPriorityText(
                    super.getDropDownView(position, convertView, parent),
                    position
                )

            private fun bindPriorityText(view: View, position: Int): View {
                val item = getItem(position)
                if (item != null) {
                    (view as TextView).text = ctx.getString(item.stringResId)
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = adapter
    }

    private fun showDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            ctx,
            { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker()
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            ctx,
            { _, hourOfDay, minute ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDateTime.set(Calendar.MINUTE, minute)
                updateDeadlineText()
            },
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDeadlineText() {
        val dateTime = LocalDateTime.of(
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH) + 1,
            selectedDateTime.get(Calendar.DAY_OF_MONTH),
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE)
        )
        deadlineEditText.setText(Converters.fromLocalDateTimeToString(dateTime))
    }
}