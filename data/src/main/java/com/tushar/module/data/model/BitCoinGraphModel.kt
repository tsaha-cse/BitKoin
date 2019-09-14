package com.tushar.module.data.model

data class BitCoinGraphModel(
    val name: String,
    val unit: String,
    val period: String,
    val values: List<DatePrice>
)

data class DatePrice(
    val date: Int,
    val dateText: String?,
    val price: Double
)