package com.tushar.module.data.datasource.local

import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

class BitCoinGraphNetworkDataSource(
    private val blockChainApi: BlockChainApi
) : BitCoinGraphDataSource {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        blockChainApi.getGraphInfo(timeSpan)
}