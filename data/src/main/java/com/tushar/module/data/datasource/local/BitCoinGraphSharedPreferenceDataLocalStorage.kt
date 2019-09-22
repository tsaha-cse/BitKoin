package com.tushar.module.data.datasource.local

import android.content.SharedPreferences
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.tushar.module.data.model.BitCoinGraphModel
import io.reactivex.Completable
import io.reactivex.Single

class BitCoinGraphSharedPreferenceDataLocalStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : BitCoinGraphLocalStorage {

    override fun getGraphInfo(timeSpan: String): Single<Pair<String, BitCoinGraphModel>> =
        sharedPreferences.getString(BIT_COIN_GRAPH_MODEL_KEY, null)?.let { string ->
            val savedLocalModel: BitCoinGraphLocalModel? = gson.fromJson(string)
            if (savedLocalModel?.timeSpan != null && savedLocalModel.bitCoinGraphModel != null) {
                Single.just(Pair(savedLocalModel.timeSpan, savedLocalModel.bitCoinGraphModel))
            } else {
                Single.error(SharedPreferenceStorageException(MSG_GENERAL_NO_DATA))
            }
        } ?: Single.error(SharedPreferenceStorageException(MSG_GENERAL_NO_DATA))

    override fun saveGraphInfo(
        timeSpan: String,
        bitCoinGraphModel: BitCoinGraphModel
    ): Completable =
        Completable.fromAction {
            sharedPreferences.edit()
                .putString(
                    BIT_COIN_GRAPH_MODEL_KEY,
                    gson.toJson(BitCoinGraphLocalModel(timeSpan, bitCoinGraphModel))
                )
                .apply()
        }

    companion object {
        private const val BIT_COIN_GRAPH_MODEL_KEY = "bit_coin_graph_model"
        private const val MSG_GENERAL_NO_DATA =
            "No bitcoin data found in shared preference data storage"
    }
}

class SharedPreferenceStorageException(msg: String) : RuntimeException(msg)

data class BitCoinGraphLocalModel(
    val timeSpan: String?,
    val bitCoinGraphModel: BitCoinGraphModel?
)