package com.dicoding.finego

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.finego.databinding.ItemListBudgetPlanningBinding

class BudgetPlanAdapter(private val budgetPlan: BudgetPlan) :
    RecyclerView.Adapter<BudgetPlanAdapter.BudgetPlanViewHolder>() {

    private val items = listOf(
        "Electricity Bill" to budgetPlan.electricity_bill,
        "Food Expenses" to budgetPlan.food_expenses,
        "Internet Cost" to budgetPlan.internet_cost,
        "Housing Cost" to budgetPlan.housing_cost,
        "Debt" to budgetPlan.debt,
        "Transportation Expenses" to budgetPlan.transportation_expenses,
        "Water Bill" to budgetPlan.water_bill
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetPlanViewHolder {
        val binding = ItemListBudgetPlanningBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BudgetPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetPlanViewHolder, position: Int) {
        val (name, amount) = items[position]
        holder.bind(name, amount)
    }

    override fun getItemCount(): Int = items.size

    class BudgetPlanViewHolder(private val binding: ItemListBudgetPlanningBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, amount: Int) {
            binding.tvItemName.text = name
            binding.tvItemAmount.text = "Rp. $amount"
        }
    }
}