package com.app.todo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.todo.databinding.TodoItemBinding

class ToDoAdapter (private val list: MutableList<ToDoData>) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>(){

    private var listener: ToDoAdapterClicksInterface? = null
    fun setListener(listener:ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    inner class ToDoViewHolder(val binding:TodoItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.toDoDescrition.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTask(this)
                }

                binding.editTask.setOnClickListener{
                    listener?.onEditTask(this)

                }
            }
        }
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteTask(toDoData: ToDoData)
        fun onEditTask(toDoData: ToDoData)
    }
}