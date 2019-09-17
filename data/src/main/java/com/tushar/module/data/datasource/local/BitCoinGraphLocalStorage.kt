package com.tushar.module.data.datasource.local

import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Completable
import io.reactivex.Single

interface BitCoinGraphLocalStorage {

    fun getGraphInfo(timeSpan: String): Single<Pair<String, BitCoinGraphModel>>

    fun saveGraphInfo(timeSpan: String, bitCoinGraphModel: BitCoinGraphModel): Completable
}