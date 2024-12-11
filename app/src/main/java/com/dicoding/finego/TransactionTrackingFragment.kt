package com.dicoding.finego

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.finego.api.ApiClient
import com.dicoding.finego.databinding.FragmentTransactionTrackingBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class TransactionTrackingFragment : Fragment() {
    private var _binding: FragmentTransactionTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory(AppModule.provideProfileRepository())
    }


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
                    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
                    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }

                    binding.tvTotalPemasukan.text = getString(R.string.total_income, totalIncome)
                    binding.tvTotalPengeluaran.text = getString(R.string.total_expense, totalExpense)
                    binding.tvSaldoNominal.text = getString(R.string.balance, totalIncome - totalExpense)
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

        lifecycleScope.launch {
            profileViewModel.profileState.collect { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE

                    is Result.Success -> {
                        binding.tvUsername.text = result.data.name
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }



    private fun fetchTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModel.fetchTransactions(userId)
            profileViewModel.fetchUserProfile(userId)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}