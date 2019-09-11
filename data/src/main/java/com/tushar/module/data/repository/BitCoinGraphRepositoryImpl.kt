package com.tushar.module.data.repository

import com.tushar.module.data.datasource.BitCoinGraphDataSource
import com.tushar.module.data.datasource.BitCoinGraphStorage
import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

class BitCoinGraphRepositoryImpl(
    private val bitCoinGraphDataSource: BitCoinGraphDataSource,
    private val bitCoinGraphStorage: BitCoinGraphStorage
) : BitCoinGraphRepository {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        bitCoinGraphStorage.getPrice(timeSpan)
            .onErrorResumeNext {
                bitCoinGraphDataSource.getGraphInfo(timeSpan)
            }
            .doOnSuccess {
                bitCoinGraphStorage.savePrice(it)
            }
}