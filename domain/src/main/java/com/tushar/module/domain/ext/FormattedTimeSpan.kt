package com.tushar.module.domain.ext

import com.tushar.module.domain.usecase.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

fun String?.toFormattedTimeSpan(): String {
    val countMatcher: Matcher = Pattern.compile("""^[^\d]*(\d+)""").matcher(this)
    var count = ""
    while (countMatcher.find()) {
        count += countMatcher.group()
    }
    val timeTextMatcher: Matcher = Pattern.compile("""[a-zA-Z]""").matcher(this)
    var timeText = ""
    while (timeTextMatcher.find()) {
        timeText += timeTextMatcher.group()
    }
    return when (timeText) {
        TimeUnit.Day.key -> "${count}D"
        TimeUnit.Week.key -> "${count}W"
        TimeUnit.Months.key -> "${count}Mo"
        TimeUnit.Year.key -> "${count}Y"
        else -> ""
    }
}