package com.tushar.module.data

import java.io.File

val price1WeekJson: String =
    File("src/test/resources/market_price_1_week.json").readText(Charsets.UTF_8)
val price1YearJson: String =
    File("src/test/resources/market_price_1_year.json").readText(Charsets.UTF_8)

const val TIME_SPAN_1_WEEK = "1weeks"
const val TIME_SPAN_1_MONTH = "1months"
const val TIME_SPAN_1_YEAR = "1years"
