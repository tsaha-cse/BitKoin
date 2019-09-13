package com.tushar.module.data.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Int.toDateTimeText(): String =
    SimpleDateFormat("dd MMM").format(Date(this * 1000L))