package com.krybed.todolist.util.task

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krybed.todolist.R
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.util.converter.Converters

abstract class BaseTaskAdapter<VH : RecyclerView.ViewHolder>(
    protected var tasks: List<Task>
) : RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int = tasks.size

    protected fun bindCommonTaskData(
        ctx: Context,
        task: Task,
        titleView: TextView,
        createdAtView: TextView,
        priorityView: TextView,
        doneView: TextView,
        attachmentIcon: ImageView
    ) {
        titleView.text = task.title

        val createdAtText = ctx.getString(
            R.string.created_prefix,
            Converters.formatLocalDateTimeToStringWithDayName(ctx, task.createdAt)
            )
        createdAtView.text = createdAtText

        val priorityText: String = ctx.getString(
            R.string.priority_prefix,
            ctx.getString(task.priority.stringResId)
        )
        priorityView.text = priorityText

        when (task.priority) {
//            Priority.HIGH -> priorityView.setTextColor(Color.RED)
//            Priority.MEDIUM -> priorityView.setTextColor(Color.rgb(255, 165, 0))
            Priority.HIGH -> priorityView.setTextColor(ContextCompat.getColor(ctx, R.color.priority_high))
            Priority.MEDIUM -> priorityView.setTextColor(ContextCompat.getColor(ctx, R.color.priority_medium))
            Priority.LOW -> priorityView.setTextColor(ContextCompat.getColor(ctx, R.color.priority_low))
//            Priority.LOW -> priorityView.setTextColor(Color.YELLOW)
        }

        doneView.visibility = if (task.isDone) View.VISIBLE else View.GONE

        val hasAttachments = task.attachments.isNotEmpty()
        attachmentIcon.visibility = if (hasAttachments) View.VISIBLE else View.GONE
    }

    fun updateTasks(newTasks: List<Task>) {
        val diffResult = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun getOldListSize(): Int = tasks.size
            override fun getNewListSize(): Int = newTasks.size
            override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean =
                tasks[oldItemPos].id == newTasks[newItemPos].id

            override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean =
                tasks[oldItemPos] == newTasks[newItemPos]
        })

        tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
    }
}