package com.dicoding.finego

data class Transaction(
    val type: String, // "income" or "expense"
    val date: String,
    val category: String,
    val amount: Int,
    val note: String? = null // Optional
)

data class TransactionRequest(
    val transactions: List<Transaction>
)

data class TransactionResponse(
    val status: String,
    val data: List<TransactionData>
)

data class TransactionData(
    val transactions: List<Transaction>,
    val date: DateTimestamp
)
data class DateTimestamp(
    val _seconds: Long,
    val _nanoseconds: Int
)