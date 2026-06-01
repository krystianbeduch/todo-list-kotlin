package com.krybed.todolist.presentation.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krybed.todolist.R
import com.krybed.todolist.data.model.Task
import com.krybed.todolist.util.converter.Converters
import com.krybed.todolist.util.task.BaseTaskAdapter

class NotificationAdapter(
    private val ctx: Context,
    tasks: List<Task>,
) : BaseTaskAdapter<NotificationAdapter.NotificationTaskViewHolder>(tasks) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationTaskViewHolder =
        NotificationTaskViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_notification_task,
                    parent,
                    false)
        )

    override fun onBindViewHolder(
        holder: NotificationTaskViewHolder,
        position: Int
    ) {
        val task = tasks[position]
        val notificationTypeText =
            task.notificationType.getTextToNotification(ctx) +
                    Converters.formatLocalDateTimeToStringWithDayName(
                        holder.itemView.context,
                        task.deadline
                    )

        holder.notificationTypeView.text = notificationTypeText
        bindCommonTaskData(
            holder.itemView.context,
            task,
            holder.titleView,
            holder.createdAtView,
            holder.priorityView,
            holder.doneView,
            holder.attachmentIcon
        )
    }

    class NotificationTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationTypeView: TextView = itemView.findViewById(R.id.notificationType)
        val titleView: TextView = itemView.findViewById(R.id.taskTitle)
        val createdAtView: TextView = itemView.findViewById(R.id.taskCreated)
        val priorityView: TextView = itemView.findViewById(R.id.taskPriority)
        val doneView: TextView = itemView.findViewById(R.id.taskDone)
        val attachmentIcon: ImageView = itemView.findViewById(R.id.attachmentIcon)
    }
}