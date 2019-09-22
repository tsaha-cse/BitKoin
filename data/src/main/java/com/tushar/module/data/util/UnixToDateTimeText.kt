package com.tushar.module.data.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

const val SECOND_IN_MILLIS = 1000L

@SuppressLint("SimpleDateFormat")
fun Int.toDateTimeText(): String =
    SimpleDateFormat("dd MMM").format(Date(this * SECOND_IN_MILLIS))