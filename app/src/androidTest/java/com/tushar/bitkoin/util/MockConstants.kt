package com.tushar.bitkoin.util

const val URL = "http://127.0.0.1"
const val PORT = 8080

object QUERY {
    const val QUERY_PARAM_NAME = "timespan"
    const val QUERY_1_WEEK = "1weeks"
    const val QUERY_1_MONTHS = "1months"
    const val QUERY_6_MONTHS = "6months"
    const val QUERY_1_YEAR = "1years"
}

object ChartLabel {
    const val CHART_1_WEEK = "Date Vs. Price($) 1W"
    const val CHART_1_MONTHS = "Date Vs. Price($) 1Mo"
    const val CHART_6_MONTHS = "Date Vs. Price($) 6Mo"
    const val CHART_1_YEAR = "Date Vs. Price($) 1Y"
}

object MockFile {
    const val MARKET_PRICE_1_WEEK = "market_price_1_week.json"
    const val MARKET_PRICE_1_MONTH = "market_price_1_year.json"
    const val MARKET_PRICE_6_MONTHS = "market_price_1_year.json"
    const val MARKET_PRICE_1_YEAR = "market_price_1_year.json"
}