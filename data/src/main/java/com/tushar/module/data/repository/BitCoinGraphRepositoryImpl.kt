package com.tushar.module.data.repository

import com.tushar.module.data.datasource.local.BitCoinGraphDataSource
import com.tushar.module.data.datasource.remote.BitCoinGraphSharedPreferenceStorageException
import com.tushar.module.data.datasource.remote.BitCoinGraphStorage
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.util.NoConnectivityException
import com.tushar.module.data.util.NoInternetException
import io.reactivex.Single

class BitCoinGraphRepositoryImpl(
    private val bitCoinGraphDataSource: BitCoinGraphDataSource,
    private val bitCoinGraphStorage: BitCoinGraphStorage
) : BitCoinGraphRepository {

    private lateinit var dataSource: DataSource

    override fun getGraphInfo(timeSpan: String): Single<BitCoinGraphInfo> =
        bitCoinGraphDataSource.getGraphInfo(timeSpan)
            .doOnSuccess {
                // save to local storage
                bitCoinGraphStorage.saveGraphInfo(it).subscribe()
                dataSource = DataSource.NetworkSource
            }
            .onErrorResumeNext { throwable ->
                when (throwable) {
                    is NoConnectivityException, is NoInternetException -> {
                        // request to local storage
                        bitCoinGraphStorage.getGraphInfo(timeSpan)
                            .doOnSuccess {
                                dataSource = DataSource.LocalSource
                            }
                    }

                    is BitCoinGraphSharedPreferenceStorageException -> {
                        Single.error(NoContentException())
                    }

                    else -> {
                        Single.error(throwable)
                    }
                }
            }.map { bitCoinGraphModel ->
                BitCoinGraphInfo(bitCoinGraphModel, dataSource)
            }

}

sealed class DataSource {
    object NetworkSource : DataSource()
    object LocalSource : DataSource()
}

data class BitCoinGraphInfo(
    val bitCoinGraphModel: BitCoinGraphModel,
    val dataSource: DataSource
)

class NoContentException : RuntimeException() {
    override val message: String?
        get() = "No information is available in both of the sources!"
}