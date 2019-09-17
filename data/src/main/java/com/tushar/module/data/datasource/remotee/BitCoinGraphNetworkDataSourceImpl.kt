package com.tushar.module.data.datasource.remotee

import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Single

class BitCoinGraphNetworkDataSourceImpl(
    private val blockChainApi: BlockChainApi
) : BitCoinGraphNetworkDataSource {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphModel> =
        blockChainApi.getGraphInfo(timeSpan)
}