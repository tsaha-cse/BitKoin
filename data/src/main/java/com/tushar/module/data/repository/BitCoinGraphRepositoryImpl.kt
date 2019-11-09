package com.tushar.module.data.repository

import com.tushar.module.data.datasource.local.BitCoinGraphLocalStorage
import com.tushar.module.data.datasource.local.SharedPreferenceStorageException
import com.tushar.module.data.datasource.remotee.BitCoinGraphNetworkDataSource
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.util.NoConnectivityException
import com.tushar.module.data.util.NoInternetException
import io.reactivex.Single

/**
 * which uses both Network[BitCoinGraphNetworkDataSource] and Local[BitCoinGraphLocalStorage]
 * as the data sources. When repository is requested to serve the data, it first looks at the
 * [BitCoinGraphNetworkDataSource] and if it succeed then it save the data to
 * [BitCoinGraphLocalStorage]. Otherwise it directly request to [BitCoinGraphLocalStorage]
 */
class BitCoinGraphRepositoryImpl(
    private val bitCoinGraphNetworkDataSource: BitCoinGraphNetworkDataSource,
    private val bitCoinGraphLocalStorage: BitCoinGraphLocalStorage
) : BitCoinGraphRepository {

    private lateinit var dataSource: DataSource
    private lateinit var timeSpan: String

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        requestToNetworkSource(timeSpan)
            .onErrorResumeNext { throwable ->
                when (throwable) {
                    is NoConnectivityException, is NoInternetException -> {
                        requestToLocalStorage(timeSpan).map { timeSpanVsGraphLocalPair ->
                            this@BitCoinGraphRepositoryImpl.timeSpan =
                                timeSpanVsGraphLocalPair.first
                            timeSpanVsGraphLocalPair.second
                        }
                    }
                    else -> {
                        Single.error(throwable)
                    }
                }
            }.map { bitCoinGraphModel ->
                BitCoinGraphInfo(
                    bitCoinGraphModel,
                    dataSource,
                    this@BitCoinGraphRepositoryImpl.timeSpan
                )
            }


    private fun requestToNetworkSource(timeSpan: String): Single<BitCoinGraphModel> =
        bitCoinGraphNetworkDataSource.getGraphInfo(timeSpan)
            .doOnSuccess { bitCoinGraphModel ->
                // save to local storage
                bitCoinGraphLocalStorage.saveGraphInfo(timeSpan, bitCoinGraphModel).subscribe()
                this@BitCoinGraphRepositoryImpl.timeSpan = timeSpan
                dataSource = DataSource.Network
            }

    private fun requestToLocalStorage(timeSpan: String): Single<Pair<String, BitCoinGraphModel>> =
        bitCoinGraphLocalStorage.getGraphInfo(timeSpan)
            .doOnSuccess {
                dataSource = DataSource.Local
            }.onErrorResumeNext { throwable ->
                when (throwable) {
                    is SharedPreferenceStorageException -> {
                        Single.error(NoContentException())
                    }
                    else -> Single.error(throwable)
                }
            }

}

sealed class DataSource {
    object Network : DataSource()
    object Local : DataSource()
}

data class BitCoinGraphInfo(
    val bitCoinGraphModel: BitCoinGraphModel,
    val dataSource: DataSource,
    val timeSpan: String
)

class NoContentException : RuntimeException() {
    override val message: String?
        get() = "No information is available in both of the sources!"
}
