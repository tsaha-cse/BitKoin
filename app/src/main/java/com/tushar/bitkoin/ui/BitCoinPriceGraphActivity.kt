package com.tushar.bitkoin.ui

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.tushar.bitkoin.R
import com.tushar.bitkoin.base.BaseActivity
import com.tushar.bitkoin.ext.px
import com.tushar.module.data.repository.BitCoinGraphInfo
import com.tushar.module.domain.ext.toFormattedTimeSpan
import com.tushar.module.presentation.viewmodel.BitCoinPriceGraphViewModel
import com.tushar.module.presentation.viewmodel.RetryRecommendation
import com.tushar.module.presentation.viewmodel.UILoadingState
import kotlinx.android.synthetic.main.activity_bit_coin_price_graph.coordinatorLayout
import kotlinx.android.synthetic.main.activity_bit_coin_price_graph.swipeRefreshLayout
import kotlinx.android.synthetic.main.activity_bit_coin_price_graph.toolbar
import kotlinx.android.synthetic.main.layout_graph.btnTimeSpan1Month
import kotlinx.android.synthetic.main.layout_graph.btnTimeSpan1Week
import kotlinx.android.synthetic.main.layout_graph.btnTimeSpan1Year
import kotlinx.android.synthetic.main.layout_graph.btnTimeSpan6Month
import kotlinx.android.synthetic.main.layout_graph.chart
import kotlinx.android.synthetic.main.layout_graph.timeSpanOptionGroup
import javax.inject.Inject


class BitCoinPriceGraphActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var bitCoinPriceGraphViewModel: BitCoinPriceGraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bit_coin_price_graph)
        setupViewModel()
        setupUi()
        setupButtonListener()
        subscribeToLiveDataChanges()
        bitCoinPriceGraphViewModel.loadBitCoinGraphModelOnLaunch()
    }

    private fun setupButtonListener() {
        btnTimeSpan1Week?.setOnClickListener {
            bitCoinPriceGraphViewModel.onClickOneWeekTimeSpan()
        }
        btnTimeSpan1Month?.setOnClickListener {
            bitCoinPriceGraphViewModel.onClickOneMonthTimeSpan()
        }
        btnTimeSpan6Month?.setOnClickListener {
            bitCoinPriceGraphViewModel.onClickSixMonthTimeSpan()
        }
        btnTimeSpan1Year?.setOnClickListener {
            bitCoinPriceGraphViewModel.onClickOneYearTimeSpan()
        }
    }


    private fun setupViewModel() {
        bitCoinPriceGraphViewModel =
            ViewModelProviders.of(this, viewModelFactory)
                .get(BitCoinPriceGraphViewModel::class.java)
    }

    private fun setupUi() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title =
            getString(R.string.bit_coin_price_activity_tool_bar_title)

        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.colorAccent
            )
        )
        swipeRefreshLayout?.setOnRefreshListener(this)

        with(chart) {
            isDragEnabled = true
            setScaleEnabled(true)
            setTouchEnabled(true)
            setPinchZoom(true)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.TOP
            setNoDataText(getString(R.string.text_chart_loading))
            setNoDataTextColor(
                ContextCompat.getColor(
                    this@BitCoinPriceGraphActivity,
                    R.color.colorPrimary
                )
            )
            legend.isEnabled = true
            description.isEnabled = false
            onChartGestureListener = SwipeRefreshControlGestureListener(swipeRefreshLayout)
        }

        timeSpanOptionGroup?.visibility = View.GONE
    }

    override fun onRefresh() {
        bitCoinPriceGraphViewModel.loadBitCoinGraphModelOnRefresh()
    }

    private fun subscribeToLiveDataChanges() {
        bitCoinPriceGraphViewModel.getLoadingStatusLiveData()
            .observe(this, Observer(::onLoadingStatusChange))
        bitCoinPriceGraphViewModel.getBitCoinGraphModelLiveData()
            .observe(this, Observer(::onDrawPriceGraph))
    }

    private fun onLoadingStatusChange(uiLoadingState: UILoadingState?) {
        uiLoadingState?.let { state ->
            swipeRefreshLayout?.isRefreshing = state.showLoader
            state.message?.let { message ->
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).apply {
                    if (state.retryRecommendation != RetryRecommendation.NO) {
                        setAction(getString(R.string.btn_retry)) {
                            bitCoinPriceGraphViewModel.loadBitCoinGraphModelOnRefresh()
                        }
                    }

                    if (state.retryRecommendation == RetryRecommendation.OPTIONAL) {
                        setActionTextColor(
                            ContextCompat.getColor(
                                this@BitCoinPriceGraphActivity,
                                R.color.colorPrimary
                            )
                        )
                    }
                    view.setPadding(0.px, 0.px, 0.px, 0.px)
                }.show()

                if (state.retryRecommendation == RetryRecommendation.MUST) {
                    chart.setNoDataText(message)
                    chart.invalidate()
                }
            }

            timeSpanOptionGroup?.visibility =
                if (state.hideTimeSpanOptions) View.GONE else View.VISIBLE
        }
    }

    private fun onDrawPriceGraph(bitCoinGraphInfo: BitCoinGraphInfo?) {
        val datePriceDot = mutableListOf<Entry>()
        val dates = mutableListOf<String>()
        bitCoinGraphInfo?.bitCoinGraphModel?.values?.let { datePriceList ->
            datePriceList.forEachIndexed { index, singleDatePrice ->
                datePriceDot.add(Entry(index.toFloat(), singleDatePrice.price.toFloat()))
                dates.add(singleDatePrice.dateText ?: "")
            }
            chart?.xAxis?.mAxisMaximum = datePriceList.size.toFloat()
            chart?.moveViewToX(0.toFloat())
        }
        val datePriceDotSet =
            LineDataSet(
                datePriceDot,
                getString(
                    R.string.text_lable_date_vs_price,
                    bitCoinGraphInfo?.timeSpan.toFormattedTimeSpan()
                )
            )
        with(datePriceDotSet) {
            color =
                ContextCompat.getColor(
                    this@BitCoinPriceGraphActivity,
                    R.color.colorPrimary
                )
            lineWidth = 3f
            circleRadius = 3f
            setDrawCircleHole(true)
            disableDashedLine()
            setCircleColor(
                ContextCompat.getColor(
                    this@BitCoinPriceGraphActivity,
                    R.color.colorPrimaryDark
                )
            )
        }
        val datePriceDotSetList = mutableListOf<ILineDataSet>()
        datePriceDotSetList.add(datePriceDotSet)
        chart?.let {
            it.data = LineData(datePriceDotSetList)
            it.xAxis?.valueFormatter = IndexAxisValueFormatter(dates.toTypedArray())
            it.xAxis?.enableGridDashedLine(10f, 10f, 0f)
            it.axisLeft?.enableGridDashedLine(10f, 10f, 0f)
            it.data?.isHighlightEnabled = false
            it.notifyDataSetChanged()
            it.animateX(1000, Easing.EasingOption.Linear)
            it.fitScreen()
            it.invalidate()
        }
    }
}
