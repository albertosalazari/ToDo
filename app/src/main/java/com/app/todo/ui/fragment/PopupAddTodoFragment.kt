package com.app.todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.app.todo.databinding.FragmentPopupAddTodoBinding

class PopupAddTodoFragment : DialogFragment() {
    private lateinit var binding: FragmentPopupAddTodoBinding
    private lateinit var listener: DialogButtonCreateTodoListeners

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPopupAddTodoBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerEvents()
    }

    private fun registerEvents() {
        binding.buttonCreateTodo.setOnClickListener {
            var todoTaskDescription = binding.editTextTodoDescription.text.toString()
            if(todoTaskDescription.isNotEmpty()) {
                listener.onSaveTask(todoTaskDescription, binding.editTextTodoDescription)
            } else {
                Toast.makeText(this.requireContext(), "Escreva uma descrição para a tarefa!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface DialogButtonCreateTodoListeners {
        fun onSaveTask(todo: String, todoEditText: EditText)
    }

    fun setListener(listener:DialogButtonCreateTodoListeners) {
        this.listener = listener
    }
}