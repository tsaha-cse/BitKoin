package com.tushar.module.data.repository

import com.tushar.module.data.datasource.local.BitCoinGraphDataSource
import com.tushar.module.data.datasource.remote.BitCoinGraphStorage
import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

class BitCoinGraphRepositoryImpl(
    private val bitCoinGraphDataSource: BitCoinGraphDataSource,
    private val bitCoinGraphStorage: BitCoinGraphStorage
) : BitCoinGraphRepository {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        bitCoinGraphStorage.getGraphInfo(timeSpan)
            .onErrorResumeNext {
                bitCoinGraphDataSource.getGraphInfo(timeSpan)
            }
            .doOnSuccess {
                bitCoinGraphStorage.saveGraphInfo(it)
            }
}