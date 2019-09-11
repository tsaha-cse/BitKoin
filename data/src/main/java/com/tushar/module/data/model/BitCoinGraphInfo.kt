package com.tushar.module.data.model

data class BitCoinGraphInfo(
    val name: String,
    val unit: String,
    val period: String,
    val values: List<DatePrice>,
    val timeStamp: Long
)

data class DatePrice(
    val date: Int,
    val dateText: String?,
    val price: Double
)