package com.dicoding.finego

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.api.ApiService
import com.dicoding.finego.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible: Boolean = false
    private lateinit var apiService: ApiService



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.instance

        setupPasswordVisibilityToggle()


        binding.btnRegister.setOnClickListener {
            registerWithEmailPassword()
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupPasswordVisibilityToggle() {
        binding.imgTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            val selection = binding.edtPassword.selectionStart
            if (isPasswordVisible) {
                binding.edtPassword.transformationMethod = null
                binding.imgTogglePassword.setImageResource(R.drawable.ic_visibility_on) // Icon mata terbuka
            } else {
                binding.edtPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                binding.imgTogglePassword.setImageResource(R.drawable.ic_visibility_off) // Icon mata tertutup
            }
            binding.edtPassword.setSelection(selection)
        }
    }




    private fun registerWithEmailPassword() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8) {
            Toast.makeText(this, "Password harus terdiri dari minimal 8 karakter", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loading.visibility = View.VISIBLE

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    callRegisterApi(email, password)
                } else {
                    binding.loading.visibility = View.GONE
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Registrasi gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun callRegisterApi(email: String, password: String) {
        val request = RegisterRequest(email, password)

        apiService.register(request)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    binding.loading.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("RegisterActivity", "API Success: ${responseBody.message}, UID: ${responseBody.uid}")
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registrasi berhasil. Silakan login.",
                                Toast.LENGTH_SHORT
                            ).show()
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Log.e("RegisterActivity", "Response body kosong")
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registrasi gagal. Respons tidak valid.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("RegisterActivity", "API response error: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            this@RegisterActivity,
                            "${response.errorBody()?.string()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    binding.loading.visibility = View.GONE
                    Log.e("RegisterActivity", "API call failed: ${t.message}", t)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Terjadi kesalahan koneksi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}