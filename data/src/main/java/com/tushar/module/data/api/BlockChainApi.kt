package com.tushar.module.data.api

import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BlockChainApi {

    @GET("/market-price?format=json")
    fun getGraphInfo(
        @Query("timespan") timeSpan: String
    ): Single<BitCoinGraphInfo>
}