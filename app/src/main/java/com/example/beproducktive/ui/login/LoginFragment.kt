package com.example.beproducktive.ui.login

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentLoginBinding
import com.example.beproducktive.ui.timer.TimerSharedViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signInClient: SignInClient
    private lateinit var callbackManager: CallbackManager


    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        handleSignInResult(result.data)
    }

    private fun handleSignInResult(data: Intent?) {
        // Result returned from launching the Sign In PendingIntent
        try {
            // Google Sign In was successful, authenticate with Firebase
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                Log.d(TAG, "firebaseAuthWithGoogle: ${credential.id}")
                firebaseAuthWithGoogle(idToken)
            } else {
                // Shouldn't happen.
                Log.d(TAG, "No ID token!")
            }
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }


    private lateinit var binding : FragmentLoginBinding

    private val sharedViewModel : TimerSharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        signInClient = Identity.getSignInClient(requireContext())

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        binding = FragmentLoginBinding.bind(view)

        // Display One-Tap Sign In if user isn't logged in
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            oneTapSignIn()
        } else {
//            Log.d(TAG, "onViewCreated: ${currentUser.displayName}")
            val destination = sharedViewModel.destinationLiveData.value
//            Log.d(TAG, "onViewCreated: $destination")

            if (destination == null) {
                Log.d(TAG, "onViewCreated: destination is null")
                val action =
                    LoginFragmentDirections.actionLoginFragmentToDailyViewTasksFragment()
                findNavController().navigate(action)
            } else {
                val newBundle = Bundle()
                newBundle.putParcelable("task", sharedViewModel.task.value)
                Log.d(TAG, "bundle: $newBundle")

                if (destination == "TIMER FRAGMENT") {
                    Log.d(TAG, "onViewCreated: destination is timer fragment")
                    val action = LoginFragmentDirections.actionLoginFragmentToTimerFragment(sharedViewModel.task.value!!)
                    findNavController().navigate(action)
                }
//                findNavController().navigate()
            }

//            val action =
//                LoginFragmentDirections.actionLoginFragmentToDailyViewTasksFragment()
//            findNavController().navigate(action)
        }

        binding.apply {

            signUpTextView.setOnClickListener {
                signUp()
            }

            signInButton.setOnClickListener {
                signIn()
            }

            signInWithGoogleButton.setOnClickListener {
                signInWithGoogle()
            }


            // It's impossible to complete the setup from facebook developer if the app is not published on the play store
            // so the facebook login is not working but it's implemented
            buttonFacebookLogin.setOnClickListener {
                Snackbar.make(requireView(), "Facebook login does not work if the app is not on the play store", Snackbar.LENGTH_SHORT).show()
                signInWithFacebook()
            }

        }

    }

    private fun signInWithFacebook() {
        binding.apply {
            binding.buttonFacebookLogin.setPermissions("email", "public_profile")
            binding.buttonFacebookLogin.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d(TAG, "facebook:onSuccess:$loginResult")
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                    override fun onCancel() {
                        Log.d(TAG, "facebook:onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(TAG, "facebook:onError", error)
                    }
                },
            )
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToDailyViewTasksFragment()
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }


    private fun signInWithGoogle() {
        val signInRequest = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        signInClient.getSignInIntent(signInRequest)
            .addOnSuccessListener { pendingIntent ->
                launchSignIn(pendingIntent)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Google Sign-in failed", e)
            }
    }

    private fun oneTapSignIn() {
        // Configure One Tap UI
        val oneTapRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build(),
            )
            .build()

        // Display the One Tap UI
        signInClient.beginSignIn(oneTapRequest)
            .addOnSuccessListener { result ->
                launchSignIn(result.pendingIntent)
            }
            .addOnFailureListener { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
            }
    }

    private fun launchSignIn(pendingIntent: PendingIntent) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent)
                .build()
            signInLauncher.launch(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            Log.e(TAG, "Couldn't start Sign In: ${e.localizedMessage}")
        }
    }



    private fun signUp() {
        binding.apply {
            val email = emailTextInput.text.toString()
            val password = passwordTextInput.editText?.text.toString()

//            Log.d("LoginFragment", "email: $email")
//            Log.d("LoginFragment", "password: $password")

            if (checkError(email, password)) {
                showSnackbar("Please fill out all fields")
                return
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToDailyViewTasksFragment()
                        findNavController().navigate(action)
                    } else {
                        val errorMessage = task.exception?.message ?: "Sign up failed"
                        showSnackbar(errorMessage)
                    }
                }
        }
    }

    private fun signIn() {
        binding.apply {
            val email = emailTextInput.text.toString()
            val password = passwordTextInput.editText?.text.toString()

            Log.d("LoginFragment", "email: $email")
            Log.d("LoginFragment", "password: $password")

            if (checkError(email, password)) {
                showSnackbar("Please fill out all fields")
                return
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val action = LoginFragmentDirections.actionLoginFragmentToDailyViewTasksFragment()
                        findNavController().navigate(action)
                    } else {
                        val errorMessage = task.exception?.message ?: "Login failed"
                        showSnackbar(errorMessage)
                    }
                }
        }
    }

    private fun checkError(email: String, password: String) : Boolean {
        return email.isEmpty() || password.isEmpty()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginFragmentKt"
    }
}