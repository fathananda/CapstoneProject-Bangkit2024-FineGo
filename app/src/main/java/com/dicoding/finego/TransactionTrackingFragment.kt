package com.dicoding.finego

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.FragmentTransactionTrackingBinding
import com.google.firebase.auth.FirebaseAuth


class TransactionTrackingFragment : Fragment() {
    private var _binding: FragmentTransactionTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        observeTransactions()
        fetchTransactions()



        binding.btnPemasukan.setOnClickListener {
            val intent = Intent(requireContext(), PemasukanActivity::class.java)
            startActivity(intent)
        }

        binding.btnPengeluaran.setOnClickListener {
            val intent = Intent(requireContext(), PengeluaranActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        fetchTransactions()
    }




    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransaction.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransaction.adapter = adapter
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(Repository(ApiClient.instance))
        viewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
    }

    private fun observeTransactions() {
        viewModel.transactions.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.llTransactionTracking.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.llTransactionTracking.visibility = View.VISIBLE
                    val transactions = result.data.data.flatMap { it.transactions }
                    if (transactions.isEmpty()) {
                        binding.rvTransaction.visibility = View.GONE
                        binding.llTransaksiKosong.visibility = View.VISIBLE
                    } else {
                        binding.rvTransaction.visibility = View.VISIBLE
                        binding.llTransaksiKosong.visibility = View.GONE
                        adapter.submitList(transactions)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.llTransactionTracking.visibility = View.VISIBLE
                    binding.rvTransaction.visibility = View.GONE
                    binding.llTransaksiKosong.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModel.fetchTransactions(userId)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}