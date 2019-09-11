package com.tushar.module.data.datasource

import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

interface BitCoinGraphDataSource {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo>
}