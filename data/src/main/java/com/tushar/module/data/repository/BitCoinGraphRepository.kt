package com.tushar.module.data.repository

import io.reactivex.Single

interface BitCoinGraphRepository {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo>
}