package com.tushar.module.data.datasource

import android.content.SharedPreferences
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.tushar.module.data.model.BitCoinGraphInfo
import io.reactivex.Single

class BitCoinGraphSharedPreferenceDataStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : BitCoinGraphStorage {

    override fun getPrice(timeSpan: String): Single<BitCoinGraphInfo> =
        sharedPreferences.getString(GRAPH_INFO_KEY, null)?.let {
            val bitCoinGraphInfo: BitCoinGraphInfo = gson.fromJson(it)
            return Single.just(bitCoinGraphInfo)
        } ?: Single.error(BitCoinGraphInfoNotFoundException())

    override fun savePrice(bitCoinGraphInfo: BitCoinGraphInfo) =
        sharedPreferences.edit()
            .putString(GRAPH_INFO_KEY, gson.toJson(bitCoinGraphInfo))
            .apply()

    companion object {
        private const val GRAPH_INFO_KEY = "graph_info_key"
    }
}

class BitCoinGraphInfoNotFoundException :
    RuntimeException("No data founds in shared preference data storage")