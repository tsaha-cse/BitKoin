package com.tushar.bitkoin.ui

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.rule.ActivityTestRule
import com.tushar.bitkoin.R
import com.tushar.bitkoin.util.ChartLabel.CHART_1_MONTHS
import com.tushar.bitkoin.util.ChartLabel.CHART_1_WEEK
import com.tushar.bitkoin.util.ChartLabel.CHART_1_YEAR
import com.tushar.bitkoin.util.ChartLabel.CHART_6_MONTHS
import com.tushar.bitkoin.util.ErrorDispatcher
import com.tushar.bitkoin.util.PORT
import com.tushar.bitkoin.util.SuccessDispatcher
import com.tushar.bitkoin.util.TestSetup
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitCoinPriceGraphActivityTest {

    private val activityTestRule =
        ActivityTestRule(BitCoinPriceGraphActivity::class.java, true, false)

    private val setup: TestSetup = TestSetup()

    @Rule
    @JvmField
    var rocketRule: TestRule = setup.getRule(this, activityTestRule)

    private val mockWebServer = MockWebServer()

    private var progressBarGoneIdlingResource: SwipeRefreshLayoutIdlingResource? = null

    @Before
    fun setup() {
        mockWebServer.start(PORT)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(progressBarGoneIdlingResource)
    }

    @Test
    fun shouldShowGraph() {
        mockWebServer.setDispatcher(SuccessDispatcher())
        activityTestRule.launchActivity(null)
        progressBarGoneIdlingResource =
            SwipeRefreshLayoutIdlingResource(
                activityTestRule.activity.findViewById(R.id.swipeRefreshLayout),
                isRefreshing = false
            )
        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertLabelDisplayed(CHART_1_WEEK)
    }

    @Test
    fun shouldShowGraphWithExpectedXAxisLabel() {
        mockWebServer.setDispatcher(SuccessDispatcher())
        activityTestRule.launchActivity(null)
        progressBarGoneIdlingResource =
            SwipeRefreshLayoutIdlingResource(
                activityTestRule.activity.findViewById(R.id.swipeRefreshLayout),
                isRefreshing = false
            )
        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertXAxisLabelsDisplayed(
                arrayOf(
                    "10 Sep",
                    "11 Sep",
                    "12 Sep",
                    "13 Sep",
                    "14 Sep",
                    "15 Sep",
                    "16 Sep"
                )
            )
    }

    @Test
    @FlakyTest
    fun shouldUpdateGraphRespondToButtonClick() {
        mockWebServer.setDispatcher(SuccessDispatcher())
        activityTestRule.launchActivity(null)
        progressBarGoneIdlingResource =
            SwipeRefreshLayoutIdlingResource(
                activityTestRule.activity.findViewById(R.id.swipeRefreshLayout),
                isRefreshing = false
            )
        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertLabelDisplayed(CHART_1_WEEK)

        BitCoinPriceGraphActivityRobot().click1MonthTimeSpanButton()

        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertLabelDisplayed(CHART_1_MONTHS)

        BitCoinPriceGraphActivityRobot().click6MonthsTimeSpanButton()

        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertLabelDisplayed(CHART_6_MONTHS)

        BitCoinPriceGraphActivityRobot().click1YearTimeSpanButton()

        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertGraphDisplayed()
            .assertLabelDisplayed(CHART_1_YEAR)
    }

    @Test
    fun shouldNotShowTimeSpanOptionsWhenThereIsAnError() {
        mockWebServer.setDispatcher(ErrorDispatcher())
        activityTestRule.launchActivity(null)
        progressBarGoneIdlingResource =
            SwipeRefreshLayoutIdlingResource(
                activityTestRule.activity.findViewById(R.id.swipeRefreshLayout),
                isRefreshing = false
            )
        BitCoinPriceGraphActivityRobot()
            .waitForCondition(progressBarGoneIdlingResource)
            .assertTimeSpanNotDisplayed()
    }
}