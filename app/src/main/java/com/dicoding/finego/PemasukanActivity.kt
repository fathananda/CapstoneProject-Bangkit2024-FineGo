package com.dicoding.finego

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.ActivityPemasukanBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


class PemasukanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPemasukanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemasukanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTanggal.setOnClickListener {
            showMaterialDatePicker()
        }

        binding.btnSimpan.setOnClickListener {
            val date = binding.etTanggal.text.toString()
            val category = binding.etKategori.text.toString()
            val amount = binding.etNominal.text.toString().toInt()
            val note = binding.etCatatan.text.toString()


            if (date.isNotEmpty() && category.isNotEmpty()) {
                val transaction = Transaction(
                    type = "income",
                    date = date,
                    category = category,
                    amount = amount,
                    note = note
                )

                submitTransaction(transaction)
            } else {
                Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun submitTransaction(transaction: Transaction) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val transactionRequest = TransactionRequest(listOf(transaction))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.addTransaction(userId, transactionRequest)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@PemasukanActivity, "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@PemasukanActivity, "Gagal menambahkan transaksi", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PemasukanActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
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
            binding.etTanggal.setText(formattedDate)
        }

        // Tampilkan dialog DatePicker
        if (!datePicker.isAdded) {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }
}