package com.dicoding.finego

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.ActivityFormBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import java.util.Calendar
import java.util.Locale

class FormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addTextWatchers()
        addSpinnerListener()

        binding.etTglLahir.setOnClickListener {
            if (binding.etTglLahir.isEnabled) {

                showMaterialDatePicker()
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (!validateInputs()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
                    binding.etEmail.error = "Masukkan email dengan format yang benar"
                }
                Toast.makeText(this, "Formulir belum lengkap atau email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitUserProfile()
        }

        updateSubmitButtonState()
    }

    private fun updateSubmitButtonState() {
        binding.btnSubmit.isEnabled = validateInputs()
    }

    private fun validateInputs(): Boolean {

        return binding.etNama.text.isNotBlank() &&
                binding.etTglLahir.text.isNotBlank() &&
                binding.etProvinsi.selectedItemPosition != 0 &&
                binding.etPenghasilan.text.isNotBlank() &&
                binding.etTransportasi.text.isNotBlank() &&
                binding.etSewa.text.isNotBlank() &&
                binding.etListrik.text.isNotBlank() &&
                binding.etAir.text.isNotBlank() &&
                binding.etInternet.text.isNotBlank() &&
                binding.etUtang.text.isNotBlank() &&
                binding.etMakan.text.isNotBlank() &&
                binding.etTabungan.text.isNotBlank() &&
                binding.etEmail.text.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()

    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSubmitButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNama.addTextChangedListener(textWatcher)
        binding.etTglLahir.addTextChangedListener(textWatcher)
        binding.etPenghasilan.addTextChangedListener(textWatcher)
        binding.etTransportasi.addTextChangedListener(textWatcher)
        binding.etSewa.addTextChangedListener(textWatcher)
        binding.etListrik.addTextChangedListener(textWatcher)
        binding.etAir.addTextChangedListener(textWatcher)
        binding.etInternet.addTextChangedListener(textWatcher)
        binding.etUtang.addTextChangedListener(textWatcher)
        binding.etMakan.addTextChangedListener(textWatcher)
        binding.etTabungan.addTextChangedListener(textWatcher)
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.etEmail.error = "Format email tidak valid"
                } else {
                    binding.etEmail.error = null
                }
                updateSubmitButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun addSpinnerListener() {
        binding.etProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSubmitButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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


    private fun submitUserProfile(){
        val nama = binding.etNama.text.toString()
        val tglLahir = binding.etTglLahir.text.toString()
        val provinsi = binding.etProvinsi.selectedItem.toString()
        val penghasilan = binding.etPenghasilan.text.toString()
        val transportasi = binding.etTransportasi.text.toString()
        val sewa = binding.etSewa.text.toString()
        val listrik = binding.etListrik.text.toString()
        val air = binding.etAir.text.toString()
        val internet = binding.etInternet.text.toString()
        val utang = binding.etUtang.text.toString()
        val makan = binding.etMakan.text.toString()
        val tabungan = binding.etTabungan.text.toString()
        val email = binding.etEmail.text.toString()


        val profile = Profile(
            name = nama,
            email = email,
            birthDate = tglLahir,
            province = provinsi,
        )

        val expense = Expense(
            food_expenses = makan.toInt(),
            transportation_expenses = transportasi.toInt(),
            housing_cost = sewa.toInt(),
            electricity_bill = listrik.toInt(),
            water_bill = air.toInt(),
            internet_cost = internet.toInt(),
            debt = utang.toInt()
        )

        val income = Income(
            total_income = penghasilan.toInt(),
            savings = tabungan.toInt()
        )

        val userInputRequest = UserInputRequest(profile, expense, income)

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        ApiClient.instance.inputUserProfile("$userId", userInputRequest)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        println("Profil berhasil disimpan")
                        Toast.makeText(this@FormActivity, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@FormActivity, MainActivity::class.java))
                        finish()
                    } else {
                        println("Gagal menyimpan profil: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            this@FormActivity, "Gagal menyimpan profil: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Error: ${t.localizedMessage}")
                }
            })


    }
}