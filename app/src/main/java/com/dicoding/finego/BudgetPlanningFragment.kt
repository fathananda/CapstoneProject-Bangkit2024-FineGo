package com.dicoding.finego

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.finego.databinding.FragmentBudgetPlanningBinding
import com.google.firebase.auth.FirebaseAuth

class BudgetPlanningFragment : Fragment() {

    private var _binding: FragmentBudgetPlanningBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetPlanningViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(AppModule.provideProfileRepository())
        viewModel = ViewModelProvider(this, factory)[BudgetPlanningViewModel::class.java]

        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        viewModel.fetchBudgetPlan(userId)

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.budgetPlan.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data.recommendations
                    val totalBudget = data.budget_plan.debt + data.budget_plan.water_bill + data.budget_plan.electricity_bill + data.budget_plan.housing_cost +  data.budget_plan.internet_cost + data.budget_plan.food_expenses + data.budget_plan.transportation_expenses
                    binding.monthlyLimit.text = getString(R.string.monthly_limit_format, data.monthly_limit.toInt())
                    binding.progressBarBatasBulanan.max = data.monthly_limit.toInt()
                    binding.progressBarBatasBulanan.progress = totalBudget

                    val savingsRate = data.savings_rate.toFloat().toInt()
                    binding.circularProgress.progress = savingsRate
                    binding.centerProgressText.text = getString(R.string.savings_rate_format, savingsRate)


                    binding.rvBudgetPlanning.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = BudgetPlanAdapter(data.budget_plan)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}