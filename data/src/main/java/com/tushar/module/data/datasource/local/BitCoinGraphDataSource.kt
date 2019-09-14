package com.tushar.module.data.datasource.local

import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Single

interface BitCoinGraphDataSource {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphModel>
}