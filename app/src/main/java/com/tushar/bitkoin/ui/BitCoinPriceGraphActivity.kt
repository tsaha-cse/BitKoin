package com.tushar.bitkoin.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tushar.bitkoin.R
import com.tushar.bitkoin.base.BaseActivity
import com.tushar.module.presentation.BitCoinPriceGraphViewModel
import kotlinx.android.synthetic.main.activity_bit_coin_price_graph.button
import javax.inject.Inject

class BitCoinPriceGraphActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var bitCoinPriceGraphViewModel: BitCoinPriceGraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bit_coin_price_graph)
        bitCoinPriceGraphViewModel =
            ViewModelProviders.of(this, viewModelFactory)
                .get(BitCoinPriceGraphViewModel::class.java)
        bitCoinPriceGraphViewModel.requestData()

        button.setOnClickListener {
            bitCoinPriceGraphViewModel.requestData()
        }
    }
}
