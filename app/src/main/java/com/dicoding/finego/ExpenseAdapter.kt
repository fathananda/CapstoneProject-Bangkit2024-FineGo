package com.dicoding.finego

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.finego.databinding.ItemExpenseBinding

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val expenses = mutableListOf<Expense>()

    fun submitList(newExpenses: List<Expense>) {
        expenses.clear()
        expenses.addAll(newExpenses)
        notifyDataSetChanged()
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun getCategoryInIndonesian(category: String): String {
            return when (category) {
                "water_bill" -> "Air"
                "electricity_bill" -> "Listrik"
                "food_expenses" -> "Makanan"
                "internet_bill" -> "Internet"
                "housing_cost" -> "Tempat Tinggal"
                "transportation_expenses" -> "Transportasi"
                "debt" -> "Hutang"
                "other" -> "Lainnya"
                else -> category
            }
        }

        fun bind(expense: Expense) {
            val categoryText = getCategoryInIndonesian(expense.category)
            binding.tvCategory.text = categoryText
            binding.tvAmount.text = "Rp ${expense.amount}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size
}