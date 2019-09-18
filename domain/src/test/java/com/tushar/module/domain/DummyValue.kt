package com.tushar.module.domain

import java.io.File

val price1WeekJson: String =
    File("src/test/resources/market_price_1_week.json").readText(Charsets.UTF_8)

const val TIME_SPAN_1_WEEK = "1weeks"