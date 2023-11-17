package com.app.todo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todo.R
import com.app.todo.databinding.FragmentLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginFragment : Fragment() {

    private lateinit var buttonFacebookLogin: LoginButton
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentLoginBinding? = null
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        val view = binding?.root

        loadInterAd()

        val registerLink =  view?.findViewById<TextView>(R.id.textViewAlreadyHaveAccount)
        registerLink?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding?.buttonLogin?.setOnClickListener {
            val email = binding?.editTextEmail?.text.toString()
            val password = binding?.editTextPassword?.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this.requireContext(), getString(R.string.email_ou_senha_incorretos), Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(this.requireContext())

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = binding?.root?.findViewById(R.id.login_button) as LoginButton
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.setFragment(this)
        buttonFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookLoginSuccess(result.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                }
            },
        )

        return view
    }

    private fun loadInterAd() {

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) {
            updateUI(currentUser)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookLoginSuccess(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(this.requireContext(),getString(R.string.email_ou_senha_incorretos),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            findNavController().navigate(R.id.action_login_to_home)
        } else {
          Toast.makeText(this.requireContext(),getString(R.string.realize_o_login_para_continuar),Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithEmailAndPassword(email:String, password: String){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if(task.isSuccessful) {
                Toast.makeText(this.requireContext(),getString(R.string.Login_realizado_com_sucesso), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_login_to_home)
                showInterAd()
            } else {
                Toast.makeText(this.requireContext(), getString(R.string.email_ou_senha_incorretos), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showInterAd() {
        if(mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    findNavController().navigate(R.id.action_login_to_home)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    findNavController().navigate(R.id.action_login_to_home)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }

            }

            mInterstitialAd?.show(requireActivity())

        }else{
            findNavController().navigate(R.id.action_login_to_home)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private var TAG = "EmailAndPassword"
    }


}

