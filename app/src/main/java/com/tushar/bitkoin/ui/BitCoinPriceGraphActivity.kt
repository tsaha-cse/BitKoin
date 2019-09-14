package com.tushar.bitkoin.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        setupListener()
        loadBitCoinGraph()
    }


    private fun setupViewModel() {
        bitCoinPriceGraphViewModel =
            ViewModelProviders.of(this, viewModelFactory)
                .get(BitCoinPriceGraphViewModel::class.java)
    }

    private fun setupUi() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.bit_coin_price_activity_tool_bar_title)

        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.colorAccent
            )
        )
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
    }

    private fun setupListener() {
        bitCoinPriceGraphViewModel.getLoadingStatusLiveData()
            .observe(this, Observer(::onLoadingStatusChange))
        bitCoinPriceGraphViewModel.getBitCoinGraphModelLiveData()
            .observe(this, Observer(::onDrawPriceGraph))
    }

    private fun loadBitCoinGraph() {
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
    }

    private fun onLoadingStatusChange(uiLoadingState: UILoadingState?) {
        uiLoadingState?.let { state ->
            swipeRefreshLayout.isRefreshing = state.showLoader
            state.message?.let {
                Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).apply {
                    if (state.retryRecommendation != RetryRecommendation.NOT_NEEDED) {
                        setAction(getString(R.string.btn_retry)) {
                            loadBitCoinGraph()
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
        bitCoinGraphModel?.let {

        }
    }
}
