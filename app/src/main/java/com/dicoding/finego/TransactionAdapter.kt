package com.dicoding.finego

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.finego.databinding.ItemListTransactionBinding

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private val transactions = mutableListOf<Transaction>()

    fun submitList(newTransactions: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemListTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvSumberPemasukan.text = transaction.category
            binding.tvNominal.text = "Rp ${transaction.amount}"
            binding.tvCatatan.text = transaction.note
            binding.tvTanggal.text = transaction.date
            binding.ivIcon.setImageResource(
                if (transaction.type == "income") R.drawable.ic_pemasukan else R.drawable.ic_pengeluaran
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }
}
