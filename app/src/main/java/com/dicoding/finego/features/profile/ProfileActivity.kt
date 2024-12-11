package com.dicoding.finego.features.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.finego.helper.AppModule
import com.dicoding.finego.helper.Result
import com.dicoding.finego.main.ViewModelFactory
import com.dicoding.finego.databinding.ActivityProfileBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory(AppModule.provideProfileRepository())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEditMode(false)

        observeProfile()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        profileViewModel.fetchUserProfile(userId)

        binding.btnEdit.setOnClickListener{
            setEditMode(true)

            binding.btnEdit.visibility = View.GONE
            binding.btnSimpan.visibility = View.VISIBLE
            binding.btnBatal.visibility = View.VISIBLE
        }

        binding.btnSimpan.setOnClickListener{
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

    private fun observeProfile() {
        lifecycleScope.launch {
            profileViewModel.profileState.collect { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        populateProfile(result.data)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@ProfileActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun populateProfile(profile: Profile) {
        binding.etNama.setText(profile.name)
        binding.etTglLahir.setText(profile.birthdate)
        binding.etProvinsi.setText(profile.province)
        binding.etEmail.setText(profile.email)
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }




    private fun setEditMode(enabled: Boolean) {
        binding.etNama.isEnabled = enabled
        binding.etTglLahir.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etProvinsi.isEnabled = enabled
    }


    private fun showMaterialDatePicker() {
        val calendar = Calendar.getInstance()

        val maxDate = calendar.timeInMillis

        calendar.add(Calendar.YEAR, -100)
        val minDate = calendar.timeInMillis

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