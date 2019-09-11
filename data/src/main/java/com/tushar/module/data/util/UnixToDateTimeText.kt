package com.tushar.module.data.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
fun Int.toDateTimeText(): String {
    val date = Date(this * 1000L)
    val expectedDateFormat = SimpleDateFormat("dd MMM")
    return try {
        val apiDateFormat = SimpleDateFormat("YYYY-MM-DDThh:mm:ss")
        apiDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        expectedDateFormat.format(apiDateFormat.format(date))
    } catch (e: IllegalArgumentException) {
        val apiDateFormat = SimpleDateFormat("YYYY-MM-DD")
        expectedDateFormat.format(apiDateFormat.format(date))
    }
}