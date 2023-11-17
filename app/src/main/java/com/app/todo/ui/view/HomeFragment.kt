package com.app.todo.ui.view

import com.app.todo.ui.adapter.ToDoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.todo.R
import com.app.todo.data.model.ToDoData
import com.app.todo.databinding.FragmentHomeBinding
import com.app.todo.ui.viewmodel.ToDoViewModel
import com.app.todo.ui.viewmodel.ToDoViewModelFactory
import com.facebook.login.LoginManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), PopupAddTodoFragment.DialogButtonCreateTodoListeners,
    ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popupFragment: PopupAddTodoFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var viewModel: ToDoViewModel

    lateinit var mAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
        loadBannerAd()

    }

    private fun loadBannerAd() {
        MobileAds.initialize(requireContext()){}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Toast.makeText(requireContext(),getString(R.string.ad_carregado), Toast.LENGTH_SHORT).show()
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(requireContext(),getString(R.string.retornando_para_o_app), Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(
            this,
            ToDoViewModelFactory(auth.currentUser?.uid.toString())
        )[ToDoViewModel::class.java]


        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ToDoAdapter()
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter

        viewModel.toDoList.observe(viewLifecycleOwner, Observer(adapter::setToDoList))
    }

    private fun registerEvents() {
        binding.addTaskButton.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = PopupAddTodoFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                PopupAddTodoFragment.TAG
            )
        }
        binding.logoutHome.setOnClickListener {
            auth.signOut()
            LoginManager.getInstance().logOut()
            navController.navigate(R.id.action_home_to_login)
        }
    }

    override fun onDeleteTask(toDoData: ToDoData) {
        viewModel.deleteTask(toDoData.taskId)
    }

    override fun onEditTask(toDoData: ToDoData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = PopupAddTodoFragment.newInstance(toDoData.taskId, toDoData.task, true)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, PopupAddTodoFragment.TAG)
    }

    override fun onSaveTask(todo: String, todoEditText: EditText) {
        viewModel.saveTask(todo)
        todoEditText.text = null
        popupFragment?.dismiss()
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEditText: EditText) {
        viewModel.updateTask(toDoData)
        todoEditText.text = null
        popupFragment!!.dismiss()
    }
}
