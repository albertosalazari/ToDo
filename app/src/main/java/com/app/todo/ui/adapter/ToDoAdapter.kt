package com.app.todo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.todo.data.model.ToDoData
import com.app.todo.databinding.TodoItemBinding
class ToDoAdapter : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    private var toDoList: List<ToDoData> = emptyList()
    private var listener: ToDoAdapterClicksInterface? = null

    fun setToDoList(newList: List<ToDoData>) {
        val diffResult = DiffUtil.calculateDiff(ToDoDiffCallback(toDoList, newList))
        this.toDoList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    inner class ToDoViewHolder(val binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(toDoList[position]) {
                binding.toDoDescrition.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTask(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTask(this)
                }
            }
        }
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteTask(toDoData: ToDoData)
        fun onEditTask(toDoData: ToDoData)
    }

    private class ToDoDiffCallback(
        private val oldList: List<ToDoData>,
        private val newList: List<ToDoData>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].taskId == newList[newItemPosition].taskId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
