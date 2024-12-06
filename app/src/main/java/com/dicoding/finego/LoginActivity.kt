package com.dicoding.finego

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPasswordVisibilityToggle()


        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            loginWithEmailPassword()
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupPasswordVisibilityToggle() {
        binding.imgTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            val selection = binding.edtPassword.selectionStart
            if (isPasswordVisible) {
                // Tampilkan password
                binding.edtPassword.transformationMethod = null
                binding.imgTogglePassword.setImageResource(R.drawable.ic_visibility_on) // Icon mata terbuka
            } else {
                // Sembunyikan password
                binding.edtPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                binding.imgTogglePassword.setImageResource(R.drawable.ic_visibility_off) // Icon mata tertutup
            }
            // Reset posisi kursor
            binding.edtPassword.setSelection(selection)
        }
    }




    private fun loginWithEmailPassword() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loading.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.loading.visibility = View.GONE
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        lifecycleScope.launch {
                            loginToApi(email, password)
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private suspend fun loginToApi(email: String, password: String) {
        val repository = Repository(ApiClient.instance)
        when (val result = repository.login(email, password)) {
            is Result.Success -> {
                Log.d(TAG, "Login API berhasil: ${result.data.token}")
                binding.loading.visibility = View.GONE
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            is Result.Error -> {
                Log.e(TAG, "Login API gagal: ${result.message}")
                binding.loading.visibility = View.GONE
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                binding.loading.visibility = View.VISIBLE
            }
        }
    }



    private fun signIn() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val userId = currentUser.uid
            checkDataProfile(userId)
        }
    }

    private fun checkDataProfile(userId: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Memulai permintaan ke API untuk userId: $userId")
                val response = withContext(Dispatchers.IO) {
                    ApiClient.instance.getUserProfile(userId)

                }
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Profil ditemukan: ${response.body()}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.e(TAG, "API gagal: ${response.code()} - ${response.message()}")
                    startActivity(Intent(this@LoginActivity, FormActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memeriksa profil: ${e.localizedMessage}")
            }
        }
    }



    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}