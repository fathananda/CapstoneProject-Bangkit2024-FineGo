package com.dicoding.finego

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.finego.databinding.ActivityProfileBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.Locale


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEditMode(false)

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


    private fun setEditMode(enabled: Boolean) {
        binding.etUsername.isEnabled = enabled
        binding.etTglLahir.isEnabled = enabled
        binding.etPekerjaan.isEnabled = enabled
        binding.etNoHandphone.isEnabled = enabled
        binding.etEmail.isEnabled = enabled
        binding.etAlamat.isEnabled = enabled
    }

    private fun saveProfile() {
        // Simpan data profil ke database atau server di sini
        val username = binding.etUsername.text.toString()
        val tglLahir = binding.etTglLahir.text.toString()
        val pekerjaan = binding.etPekerjaan.text.toString()
        val noHandphone = binding.etNoHandphone.text.toString()
        val email = binding.etEmail.text.toString()
        val alamat = binding.etAlamat.text.toString()

        // Contoh: log data (ganti dengan logika penyimpanan)
        println("Data disimpan: $username, $tglLahir, $pekerjaan, $noHandphone, $email, $alamat")
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