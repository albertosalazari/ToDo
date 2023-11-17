package com.app.todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todo.R
import com.app.todo.databinding.FragmentSignupBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentSignupBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        val view = binding?.root

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val registerLink = view?.findViewById<TextView>(R.id.textViewAlreadyHaveAccount)
        registerLink?.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        binding?.buttonRegister?.setOnClickListener {
            val email = binding?.editTextEmail?.text.toString()
            val password = binding?.editTextPassword?.text.toString()
            val confirmPassword = binding?.editTextConfirmPassword?.text.toString()

            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this.requireContext(),getString(R.string.todos_os_campos_devem_ser_preenchidos), Toast.LENGTH_SHORT).show()
            } else if(password != confirmPassword) {
                Toast.makeText(this.requireContext(),getString(R.string.senhas_nao_coincidem), Toast.LENGTH_SHORT).show()
            } else {
                createUserWithEmailAndPassword(email, password)
            }
        }

        return view
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                Toast.makeText(this.requireContext(),getString(R.string.registro_realizado_com_sucesso), Toast.LENGTH_SHORT).show()
                auth.signOut()
                LoginManager.getInstance().logOut()
                findNavController().navigate(R.id.action_register_to_login)
            }
            else {
                Toast.makeText(this.requireContext(),getString(R.string.nao_foi_possivel_realizar_o_cadastro), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}