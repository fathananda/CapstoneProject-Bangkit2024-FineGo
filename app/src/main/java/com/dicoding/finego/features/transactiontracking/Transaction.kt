package com.dicoding.finego.features.transactiontracking

data class Transaction(
    val type: String,
    val date: String,
    val category: String,
    val amount: Int,
    val note: String? = null
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