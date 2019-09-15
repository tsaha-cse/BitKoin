package com.tushar.bitkoin.ui

import android.os.Bundle
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
import com.tushar.module.data.model.BitCoinGraphModel
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
        }

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
                    if (state.retryRecommendation != RetryRecommendation.NOT_NEEDED) {
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
                }.show()
            }
        }
    }

    private fun onDrawPriceGraph(bitCoinGraphModel: BitCoinGraphModel?) {
        val datePriceDot = mutableListOf<Entry>()
        val dates = mutableListOf<String>()
        bitCoinGraphModel?.values?.let { datePrice ->
            datePrice.forEachIndexed { index, singleDatePrice ->
                datePriceDot.add(Entry(index.toFloat(), singleDatePrice.price.toFloat()))
                dates.add(singleDatePrice.dateText ?: "")
            }
        }
        val datePriceDotSet =
            LineDataSet(datePriceDot, getString(R.string.text_lable_date_vs_price, "1W"))
        datePriceDotSet.color =
            ContextCompat.getColor(
                this@BitCoinPriceGraphActivity,
                R.color.colorPrimary
            )
        datePriceDotSet.lineWidth = 3f
        datePriceDotSet.disableDashedLine()
        datePriceDotSet.setCircleColor(
            ContextCompat.getColor(
                this@BitCoinPriceGraphActivity,
                R.color.colorPrimaryDark
            )
        )
        val datePriceDotSetList = mutableListOf<ILineDataSet>()
        datePriceDotSetList.add(datePriceDotSet)
        chart?.data = LineData(datePriceDotSetList)
        chart?.xAxis?.valueFormatter = IndexAxisValueFormatter(dates.toTypedArray())
        chart?.let {
            it.invalidate()
            it.animateX(1000, Easing.EasingOption.Linear)
        }
    }
}
