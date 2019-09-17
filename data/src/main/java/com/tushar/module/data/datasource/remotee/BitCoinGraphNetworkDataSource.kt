package com.tushar.module.data.datasource.remotee

import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Single

interface BitCoinGraphNetworkDataSource {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphModel>
}