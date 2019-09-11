package com.tushar.module.data.datasource

import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

interface BitCoinGraphStorage {

    fun getPrice(timeSpan: String): Single<BitCoinGraphInfo>

    fun savePrice(bitCoinGraphInfo: BitCoinGraphInfo)
}