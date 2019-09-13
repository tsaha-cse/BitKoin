package com.tushar.module.data.datasource.remote

import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

interface BitCoinGraphStorage {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo>

    fun saveGraphInfo(bitCoinGraphInfo: BitCoinGraphInfo)
}