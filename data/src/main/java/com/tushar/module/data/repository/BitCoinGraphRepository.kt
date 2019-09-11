package com.tushar.module.data.repository

import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

interface BitCoinGraphRepository {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo>
}