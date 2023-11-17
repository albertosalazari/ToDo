package com.app.todo.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.app.todo.R
import com.app.todo.data.model.ToDoData
import com.app.todo.databinding.FragmentPopupAddTodoBinding

class PopupAddTodoFragment : DialogFragment() {
    private lateinit var binding: FragmentPopupAddTodoBinding
    private lateinit var listener: DialogButtonCreateTodoListeners
    private var  toDoData: ToDoData? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPopupAddTodoBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null) {
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )

            binding.editTextTodoDescription.setText(toDoData?.task)
            binding.buttonCreateTodo.text = getString(R.string.atualizar) // Altera o texto do botão

        }

        registerEvents()
    }

    private fun registerEvents() {
        binding.buttonCreateTodo.setOnClickListener {
            val todoTaskDescription = binding.editTextTodoDescription.text.toString()
            if(todoTaskDescription.isNotEmpty()) {
                if(toDoData == null) {
                    listener.onSaveTask(todoTaskDescription, binding.editTextTodoDescription)

                } else {
                    toDoData?.task = todoTaskDescription
                    listener.onUpdateTask(toDoData!!,binding.editTextTodoDescription)
                }
            } else {
                Toast.makeText(this.requireContext(), "Escreva uma descrição para a tarefa!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    interface DialogButtonCreateTodoListeners {
        fun onSaveTask(todo: String, todoEditText: EditText)
        fun onUpdateTask(toDoData: ToDoData, todoEditText: EditText)

    }

    fun setListener(listener: DialogButtonCreateTodoListeners) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String, isUpdate:Boolean = false) = PopupAddTodoFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }

            if (isUpdate) {
                arguments?.putBoolean("isUpdate", true)
            }
        }
    }
}