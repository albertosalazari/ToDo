package com.app.todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.todo.R
import com.app.todo.databinding.FragmentHomeBinding
import com.app.todo.ui.adapter.ToDoAdapter
import com.app.todo.ui.adapter.ToDoData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), PopupAddTodoFragment.DialogButtonCreateTodoListeners,
    ToDoAdapter.ToDoAdapterClicksInterface {
    private lateinit var auth:FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popupFragment: PopupAddTodoFragment? = null
    private lateinit var adapter:ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
        getDataFromFireBase()
    }

    private fun registerEvents() {
        binding.addTaskButton.setOnClickListener {
            if(popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = PopupAddTodoFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                PopupAddTodoFragment.TAG
            )
        }


    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

        private fun getDataFromFireBase() {
            databaseRef.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mList.clear()
                    for (taskSnapshot in snapshot.children) {
                        val toDoTask = taskSnapshot.key?.let {
                            ToDoData(it, taskSnapshot.value.toString())
                        }

                        if(toDoTask != null) {
                            mList.add(toDoTask)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }


        override fun onSaveTask(todo: String, todoEditText: EditText) {
            databaseRef.push().setValue(todo).addOnCompleteListener{
                if(it.isSuccessful) {
                    Toast.makeText(context, getString(R.string.tarefa_salva_com_sucesso), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
                todoEditText.text = null
                popupFragment?.dismiss()
            }
        }

    override fun onUpdateTask(toDoData: ToDoData, todoEditText: EditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(context,"Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT) .show()

            } else {
              Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT) .show()
            }
            todoEditText.text = null
            popupFragment!!.dismiss()
        }
    }

    override fun onDeleteTask(toDoData: ToDoData) {
            databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener{
                if(it.isSuccessful) {
                    Toast.makeText(context, getString(R.string.tarefa_deletada_corretamente), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onEditTask(toDoData: ToDoData) {
            if(popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

            popupFragment = PopupAddTodoFragment.newInstance(toDoData.taskId, toDoData.task)
            popupFragment!!.setListener(this)
            popupFragment!!.show(childFragmentManager,PopupAddTodoFragment.TAG)
        }
    }

