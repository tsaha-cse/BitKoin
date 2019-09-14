package com.tushar.module.data.datasource.remote

import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Completable
import io.reactivex.Single

interface BitCoinGraphStorage {

    fun getGraphInfo(timeSpan: String): Single<BitCoinGraphModel>

    fun saveGraphInfo(bitCoinGraphModel: BitCoinGraphModel) : Completable
}