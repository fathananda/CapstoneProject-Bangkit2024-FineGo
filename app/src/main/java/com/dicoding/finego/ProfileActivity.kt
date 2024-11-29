package com.dicoding.finego

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.ActivityProfileBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEditMode(false)

        getProfile()

        binding.btnEdit.setOnClickListener{
            setEditMode(true)

            binding.btnEdit.visibility = View.GONE
            binding.btnSimpan.visibility = View.VISIBLE
            binding.btnBatal.visibility = View.VISIBLE
        }

        binding.btnSimpan.setOnClickListener{
            saveProfile()
            setEditMode(false)

            binding.btnEdit.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.GONE
            binding.btnBatal.visibility = View.GONE
        }

        binding.btnBatal.setOnClickListener {
            setEditMode(false)

            binding.btnEdit.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.GONE
            binding.btnBatal.visibility = View.GONE
        }

        binding.etTglLahir.setOnClickListener {
            if (binding.etTglLahir.isEnabled) {
                showMaterialDatePicker()
            }
        }
    }

    private fun getProfile() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()
        ApiClient.instance.getUserProfile(userId)
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()?.data
                        if (userProfile != null) {
                            binding.etNama.setText(userProfile.name ?: "")
                            binding.etTglLahir.setText(userProfile.birthDate ?: "")
                            binding.etProvinsi.setText(userProfile.province ?: "")
                            binding.etPenghasilan.setText(userProfile.income.toString() ?: "")
                            binding.etEmail.setText(userProfile.email ?: "")
                            binding.etTabungan.setText(userProfile.savings.toString() ?: "")
                        } else {
                            println("Profil kosong atau tidak ditemukan.")
                        }
                    } else {
                        println("Gagal mendapatkan profil: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    println("Error: ${t.localizedMessage}")
                }
            })
    }



    private fun setEditMode(enabled: Boolean) {
        binding.etNama.isEnabled = enabled
        binding.etTglLahir.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etProvinsi.isEnabled = enabled
        binding.etPenghasilan.isEnabled = enabled
        binding.etTabungan.isEnabled = enabled
    }

    private fun saveProfile() {
        // Simpan data profil ke database atau server di sini
        val nama = binding.etNama.text.toString()
        val tglLahir = binding.etTglLahir.text.toString()
        val email = binding.etEmail.text.toString()
        val provinsi = binding.etProvinsi.text.toString()
        val penghasilan = binding.etPenghasilan.text.toString()
        val tabungan = binding.etTabungan.text.toString()

    }

    private fun showMaterialDatePicker() {
        val calendar = Calendar.getInstance()

        // Batas maksimal (hari ini)
        val maxDate = calendar.timeInMillis

        // Batas minimal (opsional: 100 tahun ke belakang)
        calendar.add(Calendar.YEAR, -100)
        val minDate = calendar.timeInMillis

        // Atur Constraint
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(maxDate)) // Tidak bisa pilih masa depan
            .setStart(minDate) // Mulai dari 100 tahun lalu
            .setEnd(maxDate) // Hingga hari ini

        // Atur builder DatePicker
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal Lahir")
            .setSelection(maxDate) // Default tanggal hari ini
            .setCalendarConstraints(constraintsBuilder.build()) // Tambahkan batas
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR) // Tampilkan langsung kalender

        val datePicker = builder.build()

        // Handle tanggal yang dipilih
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Calendar.getInstance()
            selectedDate.timeInMillis = selection

            // Format tanggal
            val formattedDate = String.format(
                Locale.getDefault(),
                "%02d/%02d/%d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR)
            )

            // Set tanggal ke EditText
            binding.etTglLahir.setText(formattedDate)
        }

        // Tampilkan dialog DatePicker
        if (!datePicker.isAdded) {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }



}