package com.app.todo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.todo.data.model.ToDoData
import com.app.todo.data.repository.ToDoRepository

class ToDoViewModel(private val userId: String) : ViewModel() {

    private val repository = ToDoRepository(userId)

    val toDoList: LiveData<List<ToDoData>> = repository.getData()

    fun saveTask(todo: String) {
        repository.saveTask(todo)
    }

    fun updateTask(toDoData: ToDoData) {
        repository.updateTask(toDoData)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }
}
