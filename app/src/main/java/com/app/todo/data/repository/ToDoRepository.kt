package com.app.todo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.todo.data.model.ToDoData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ToDoRepository(private val userId: String) {

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(userId)

    fun getData(): LiveData<List<ToDoData>> {
        val toDoList = MutableLiveData<List<ToDoData>>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ToDoData>()
                for (taskSnapshot in snapshot.children) {
                    val toDoTask = taskSnapshot.key?.let {
                        ToDoData(it, taskSnapshot.value.toString())
                    }

                    toDoTask?.let { list.add(it) }
                }
                toDoList.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return toDoList
    }

    fun saveTask(todo: String) {
        databaseRef.push().setValue(todo)
    }

    fun updateTask(toDoData: ToDoData) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map)
    }

    fun deleteTask(taskId: String) {
        databaseRef.child(taskId).removeValue()
    }
}
