package com.tushar.module.data.datasource.remote

import android.content.SharedPreferences
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Completable
import io.reactivex.Single

class BitCoinGraphSharedPreferenceDataStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : BitCoinGraphStorage {

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphModel> =
        sharedPreferences.getString(BIT_COIN_GRAPH_MODEL_KEY, null)?.let { string ->
            return Single.just(gson.fromJson(string))
        } ?: Single.error(BitCoinGraphSharedPreferenceStorageException())

    override fun saveGraphInfo(bitCoinGraphModel: BitCoinGraphModel): Completable =
        Completable.fromAction {
            sharedPreferences.edit()
                .putString(BIT_COIN_GRAPH_MODEL_KEY, gson.toJson(bitCoinGraphModel))
                .apply()
        }

    companion object {
        private const val BIT_COIN_GRAPH_MODEL_KEY = "bit_coin_graph_model_key"
    }
}

class BitCoinGraphSharedPreferenceStorageException :
    RuntimeException("No bit coin data found in shared preference data storage")