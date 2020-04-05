package com.tushar.module.data.datasource.remotee

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.tushar.module.data.TIME_SPAN_1_WEEK
import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.base.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify

class BitCoinGraphNetworkDataSourceImplTest : BaseTest() {

    @Mock
    lateinit var blockChainApi: BlockChainApi

    private lateinit var bitCoinGraphNetworkDataSourceImpl: BitCoinGraphNetworkDataSourceImpl

    private val timeSpanCaptor = argumentCaptor<String>()

    @Before
    fun setUp() {
        bitCoinGraphNetworkDataSourceImpl =
            BitCoinGraphNetworkDataSourceImpl(blockChainApi)
    }

    @Test
    fun `Block chain API should be requested with the same value that data source is requested for`() {
        bitCoinGraphNetworkDataSourceImpl.getGraphInfo(TIME_SPAN_1_WEEK)
        verify(blockChainApi).getGraphInfo(timeSpanCaptor.capture())
        assertEquals(TIME_SPAN_1_WEEK, timeSpanCaptor.firstValue)
    }
}
