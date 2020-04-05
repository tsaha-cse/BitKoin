package com.tushar.module.domain.ext

import com.tushar.module.domain.usecase.TimeSpanUnit
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
        TimeSpanUnit.Day.key -> "${count}D"
        TimeSpanUnit.Week.key -> "${count}W"
        TimeSpanUnit.Months.key -> "${count}Mo"
        TimeSpanUnit.Year.key -> "${count}Y"
        else -> ""
    }
}
