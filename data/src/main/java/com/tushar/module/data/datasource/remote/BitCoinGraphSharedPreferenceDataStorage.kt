package com.tushar.module.data.datasource.remote

import android.content.SharedPreferences
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

class BitCoinGraphSharedPreferenceDataStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : BitCoinGraphStorage {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        sharedPreferences.getString(BIT_COIN_GRAPH_INFO_KEY, null)?.let {
            val bitCoinGraphInfo: BitCoinGraphInfo = gson.fromJson(it)
            return Single.just(bitCoinGraphInfo)
        } ?: Single.error(BitCoinGraphInfoNotFoundException())

    override fun saveGraphInfo(bitCoinGraphInfo: BitCoinGraphInfo) =
        sharedPreferences.edit()
            .putString(BIT_COIN_GRAPH_INFO_KEY, gson.toJson(bitCoinGraphInfo))
            .apply()

    companion object {
        private const val BIT_COIN_GRAPH_INFO_KEY = "bit_coin_graph_info_key"
    }
}

class BitCoinGraphInfoNotFoundException :
    RuntimeException("No bit coin data found in shared preference data storage")