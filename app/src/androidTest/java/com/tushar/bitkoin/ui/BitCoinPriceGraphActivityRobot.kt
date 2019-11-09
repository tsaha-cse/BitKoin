package com.tushar.bitkoin.ui

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tushar.bitkoin.R
import org.hamcrest.Description
import org.hamcrest.Matchers.not


class BitCoinPriceGraphActivityRobot {

    fun click1MonthTimeSpanButton() = apply {
        onView(buttonViewMatcher1Month).perform(click())
    }

    fun click6MonthsTimeSpanButton() = apply {
        onView(buttonViewMatcher6Month).perform(click())
    }

    fun click1YearTimeSpanButton() = apply {
        onView(buttonViewMatcher1Year).perform(click())
    }

    fun assertGraphDisplayed() = apply {
        onView(chartGraphIdViewMatcher).check(matches(isDisplayed()))
    }

    fun assertTimeSpanNotDisplayed() = apply {
        onView(timeSpanOptionGroupViewMatcher).check(matches(not(isDisplayed())))
    }

    fun assertLabelDisplayed(label: String) = apply {
        onView(chartGraphIdViewMatcher).check(matches(LineChartLabelMatcher(label)))
    }

    fun assertXAxisLabelsDisplayed(labels: Array<String>) = apply {
        onView(chartGraphIdViewMatcher).check(matches(LineChartXAxisLabelMatcher(labels)))
    }

    fun waitForCondition(idlingResource: IdlingResource?) = apply {
        IdlingRegistry.getInstance().register(idlingResource)
    }

    class LineChartLabelMatcher(private val label: String) :
        BoundedMatcher<View, LineChart>(LineChart::class.java) {

        override fun describeTo(description: Description?) {
            description?.appendText("Checking the label of the view with the expected text")
        }

        override fun matchesSafely(item: LineChart?): Boolean {
            var isDisplayed = false
            item?.data?.dataSetLabels?.forEach {
                isDisplayed = (it == label)
            }
            return isDisplayed
        }
    }

    class LineChartXAxisLabelMatcher(private val labels: Array<String>) :
        BoundedMatcher<View, LineChart>(LineChart::class.java) {

        override fun describeTo(description: Description?) {
            description?.appendText("Checking the label of the XAxis")
        }

        override fun matchesSafely(item: LineChart?): Boolean =
            (item?.xAxis?.valueFormatter as? IndexAxisValueFormatter)
                ?.values?.contentEquals(labels) ?: false
    }

    companion object {
        private const val buttonIdTimeSpan6Month = R.id.btnTimeSpan6Month
        private val buttonViewMatcher6Month = withId(buttonIdTimeSpan6Month)

        private const val buttonIdTimeSpan1Year = R.id.btnTimeSpan1Year
        private val buttonViewMatcher1Year = withId(buttonIdTimeSpan1Year)

        private const val buttonIdTimeSpan1Month = R.id.btnTimeSpan1Month
        private val buttonViewMatcher1Month = withId(buttonIdTimeSpan1Month)

        private const val chartGraphId = R.id.chart
        private val chartGraphIdViewMatcher = withId(chartGraphId)

        private const val timeSpanOptionGroup = R.id.timeSpanOptionGroup
        private val timeSpanOptionGroupViewMatcher = withId(timeSpanOptionGroup)
    }
}
