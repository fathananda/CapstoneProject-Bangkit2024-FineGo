package com.dicoding.finego

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.finego.databinding.FragmentMonthlyReportBinding
import com.google.firebase.auth.FirebaseAuth

class MonthlyReportFragment : Fragment() {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MonthlyReportViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val repository = AppModule.provideProfileRepository()
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[MonthlyReportViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchMonthlyReport(userId)
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter()
        binding.rvHighestExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHighestExpenses.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.monthlyReport.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvLabelKeuangan.text = getString(R.string.label_keuangan, result.data.financeReport.financeStatus)
                    adapter.submitList(result.data.financeReport.highestExpenses)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}