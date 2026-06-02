package com.krybed.todolist.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krybed.todolist.R
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.util.converter.Converters
import com.krybed.todolist.util.task.BaseTaskAdapter

class TaskAdapter(
    tasks: List<Task>,
    private val listener: OnTaskClickListener
) : BaseTaskAdapter<TaskAdapter.TaskViewHolder>(tasks) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder =
        TaskViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_task,
                    parent,
                    false
                )
        )

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val task = tasks[position]
        val deadlineText = holder.itemView.context.getString(
            R.string.deadline_prefix
        ) + " " + Converters.formatLocalDateTimeToStringWithDayName(
            holder.itemView.context,
            task.deadline
        )

        holder.deadlineView.text = deadlineText

        bindCommonTaskData(
            holder.itemView.context,
            task,
            holder.titleView,
            holder.createdAtView,
            holder.priorityView,
            holder.doneView,
            holder.attachmentIcon
        )

        holder.itemView.setOnClickListener { view ->
            val popupMenu = PopupMenu(holder.itemView.context, view)
            popupMenu.inflate(R.menu.task_context_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit_task -> {
                        listener.onEditClick(task)
                        true
                    }
                    R.id.delete_task -> {
                        listener.onDeleteClick(task)
                        true
                    }
                    R.id.change_status -> {
                        listener.onChangeStatusClick(task)
                        true
                    }
                    R.id.add_attachment -> {
                        listener.onAddAttachmentClick(task)
                        true
                    }
                    R.id.show_attachment -> {
                        listener.onShowAttachmentClick(task)
                        true
                    }
                    R.id.delete_attachment -> {
                        listener.onDeleteAttachmentClick(task)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.itemView.setOnLongClickListener {
            listener.onLongClick(task)
            true
        }
    }

    // to del raczej
//    fun notifyTaskChanged(taskId: Int) {
//        val position = tasks.indexOfFirst { it.id == taskId }
//        if (position != -1) {
//            notifyItemChanged(position)
//        }
//    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.taskTitle)
        val deadlineView: TextView = itemView.findViewById(R.id.taskDeadline)
        val createdAtView: TextView = itemView.findViewById(R.id.taskCreated)
        val priorityView: TextView = itemView.findViewById(R.id.taskPriority)
        val doneView: TextView = itemView.findViewById(R.id.taskDone)
        val attachmentIcon: ImageView = itemView.findViewById(R.id.attachmentIcon)
    }

    interface OnTaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onChangeStatusClick(task: Task)
        fun onLongClick(task: Task)
        fun onAddAttachmentClick(task: Task)
        fun onDeleteAttachmentClick(task: Task)
        fun onShowAttachmentClick(task: Task)
    }
}