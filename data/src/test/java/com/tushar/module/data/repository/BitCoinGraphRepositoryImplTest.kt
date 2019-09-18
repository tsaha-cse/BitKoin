package com.tushar.module.data.repository

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tushar.module.data.TIME_SPAN_1_MONTH
import com.tushar.module.data.TIME_SPAN_1_WEEK
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.base.BaseTest
import com.tushar.module.data.datasource.local.BitCoinGraphLocalStorage
import com.tushar.module.data.datasource.local.SharedPreferenceStorageException
import com.tushar.module.data.datasource.remotee.BitCoinGraphNetworkDataSource
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.price1WeekJson
import com.tushar.module.data.util.NoConnectivityException
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class BitCoinGraphRepositoryImplTest : BaseTest() {

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphModel::class.java, BitCoinGraphInfoDeserializer())
            .create()

    private lateinit var bitCoinGraphModelFor1Week: BitCoinGraphModel

    private lateinit var bitCoinGraphInfo: BitCoinGraphInfo

    @Mock
    lateinit var bitCoinGraphNetworkDataSource: BitCoinGraphNetworkDataSource

    @Mock
    lateinit var bitCoinGraphLocalStorage: BitCoinGraphLocalStorage

    lateinit var bitCoinGraphRepository: BitCoinGraphRepositoryImpl

    @Before
    fun setUp() {
        bitCoinGraphRepository = BitCoinGraphRepositoryImpl(
            bitCoinGraphNetworkDataSource,
            bitCoinGraphLocalStorage
        )

        bitCoinGraphModelFor1Week = gson.fromJson(price1WeekJson)

        bitCoinGraphInfo = BitCoinGraphInfo(
            bitCoinGraphModelFor1Week,
            DataSource.Network,
            TIME_SPAN_1_WEEK
        )
    }

    @Test
    fun `Should return graph info from network data source`() {
        whenever(bitCoinGraphNetworkDataSource.getGraphInfo(any())).thenReturn(
            Single.just(bitCoinGraphModelFor1Week)
        )

        whenever(
            bitCoinGraphLocalStorage.saveGraphInfo(
                any(),
                any()
            )
        ).thenReturn(Completable.complete())

        bitCoinGraphRepository.getGraphInfo(TIME_SPAN_1_WEEK)
            .test()
            .assertValue(bitCoinGraphInfo)
    }

    @Test
    fun `Should save to local storage when return from network data source`() {
        whenever(bitCoinGraphNetworkDataSource.getGraphInfo(any())).thenReturn(
            Single.just(bitCoinGraphModelFor1Week)
        )

        whenever(
            bitCoinGraphLocalStorage.saveGraphInfo(
                any(),
                any()
            )
        ).thenReturn(Completable.complete())
        bitCoinGraphRepository.getGraphInfo(TIME_SPAN_1_WEEK).test()
        verify(bitCoinGraphLocalStorage).saveGraphInfo(TIME_SPAN_1_WEEK, bitCoinGraphModelFor1Week)

    }

    @Test
    fun `Should return 'NoContentException' when no data is found in any of the sources`() {
        whenever(bitCoinGraphNetworkDataSource.getGraphInfo(any())).thenReturn(
            Single.error(NoConnectivityException())
        )

        whenever(bitCoinGraphLocalStorage.getGraphInfo(any())).thenReturn(
            Single.error(SharedPreferenceStorageException(""))
        )

        bitCoinGraphRepository.getGraphInfo(TIME_SPAN_1_MONTH)
            .test()
            .assertError(NoContentException::class.java)
    }

    @Test
    fun `Should return from local storage when network source is not available`() {
        whenever(bitCoinGraphNetworkDataSource.getGraphInfo(any())).thenReturn(
            Single.error(NoConnectivityException())
        )

        whenever(bitCoinGraphLocalStorage.getGraphInfo(TIME_SPAN_1_WEEK)).thenReturn(
            Single.just(Pair(TIME_SPAN_1_WEEK, bitCoinGraphModelFor1Week))
        )

        bitCoinGraphInfo = BitCoinGraphInfo(
            bitCoinGraphModelFor1Week,
            DataSource.Local,
            TIME_SPAN_1_WEEK
        )

        bitCoinGraphRepository.getGraphInfo(TIME_SPAN_1_WEEK)
            .test()
            .assertValue(bitCoinGraphInfo)
    }
}