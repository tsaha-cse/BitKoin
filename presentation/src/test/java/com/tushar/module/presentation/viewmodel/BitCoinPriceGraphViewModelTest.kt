package com.tushar.module.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.repository.BitCoinGraphInfo
import com.tushar.module.data.repository.DataSource
import com.tushar.module.data.repository.NoContentException
import com.tushar.module.domain.base.ParamValidationException
import com.tushar.module.domain.usecase.GetBitCoinGraphInfoUseCase
import com.tushar.module.domain.usecase.TimeSpan
import com.tushar.module.domain.usecase.TimeSpanUnit
import com.tushar.module.presentation.TIME_SPAN_1_WEEK
import com.tushar.module.presentation.TIME_SPAN_1_YEAR
import com.tushar.module.presentation.base.observeOnce
import com.tushar.module.presentation.price1WeekJson
import com.tushar.module.presentation.price1YearJson
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class BitCoinPriceGraphViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    var mockitoRule: MockitoRule? = MockitoJUnit.rule()

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphModel::class.java, BitCoinGraphInfoDeserializer())
            .create()

    @Mock
    lateinit var getBitCoinGraphInfoUseCase: GetBitCoinGraphInfoUseCase

    private lateinit var bitCoinPriceGraphViewModel: BitCoinPriceGraphViewModel

    private var timeSpanCaptor = argumentCaptor<TimeSpan>()

    @Test
    fun `Should request with default one week time span`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModelOnLaunch()
        assertEquals(TimeSpan(1, TimeSpanUnit.Week), timeSpanCaptor.firstValue)
    }

    @Test
    fun `Should request with recent timespan when reload requested`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.onClickOneMonthTimeSpan()
        bitCoinPriceGraphViewModel.loadBitCoinGraphModelOnRefresh()
        assertEquals(TimeSpan(1, TimeSpanUnit.Months), timeSpanCaptor.firstValue)
    }

    @Test
    fun `Should request with one week time span`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.onClickOneWeekTimeSpan()
        assertEquals(TimeSpan(1, TimeSpanUnit.Week), timeSpanCaptor.firstValue)
    }

    @Test
    fun `Should request with one month time span`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.onClickOneMonthTimeSpan()
        assertEquals(TimeSpan(1, TimeSpanUnit.Months), timeSpanCaptor.firstValue)
    }

    @Test
    fun `Should request with six month time span`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.onClickSixMonthTimeSpan()
        assertEquals(TimeSpan(6, TimeSpanUnit.Months), timeSpanCaptor.firstValue)
    }

    @Test
    fun `Should request with one year time span`() {
        whenever(getBitCoinGraphInfoUseCase(timeSpanCaptor.capture()))
            .thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.onClickOneYearTimeSpan()
        assertEquals(TimeSpan(1, TimeSpanUnit.Year), timeSpanCaptor.firstValue)
    }


    @Test
    fun `Should return graph info`() {
        val bitCoinGraphModelFor1Week: BitCoinGraphModel = gson.fromJson(price1WeekJson)
        val bitCoinGraphInfo = BitCoinGraphInfo(
            bitCoinGraphModelFor1Week,
            DataSource.Network,
            TIME_SPAN_1_WEEK
        )
        whenever(getBitCoinGraphInfoUseCase(any())).thenReturn(Single.just(bitCoinGraphInfo))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
        bitCoinPriceGraphViewModel.getBitCoinGraphModelLiveData().observeOnce {
            assertEquals(bitCoinGraphInfo, it)
        }
    }

    @Test
    fun `Should return graph info and send message that the source is local`() {
        val bitCoinGraphModelFor1Week: BitCoinGraphModel = gson.fromJson(price1YearJson)
        val bitCoinGraphInfo = BitCoinGraphInfo(
            bitCoinGraphModelFor1Week,
            DataSource.Local,
            TIME_SPAN_1_YEAR
        )
        whenever(getBitCoinGraphInfoUseCase(any())).thenReturn(Single.just(bitCoinGraphInfo))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
        bitCoinPriceGraphViewModel.getBitCoinGraphModelLiveData().observeOnce {
            assertEquals(bitCoinGraphInfo, it)
        }

        bitCoinPriceGraphViewModel.getLoadingStatusLiveData().observeOnce {
            assertEquals(RECOMMENDED_RELOAD_TEXT, it.message)
        }
    }

    @Test
    fun `Should send no internet message when there is no internet connection`() {
        whenever(getBitCoinGraphInfoUseCase(any())).thenReturn(Single.error(NoContentException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
        bitCoinPriceGraphViewModel.getLoadingStatusLiveData().observeOnce {
            assertEquals(ERROR_MSG_NO_INTERNET, it.message)
        }
    }


    @Test
    fun `Should send validation failed message when there is ParamValidationException`() {
        whenever(getBitCoinGraphInfoUseCase(any())).thenReturn(Single.error(ParamValidationException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
        bitCoinPriceGraphViewModel.getLoadingStatusLiveData().observeOnce {
            assertEquals(ERROR_VALIDATION, it.message)
        }
    }

    @Test
    fun `Should send something went wrong message when there is an Exception`() {
        whenever(getBitCoinGraphInfoUseCase(any())).thenReturn(Single.error(RuntimeException()))
        bitCoinPriceGraphViewModel = BitCoinPriceGraphViewModel(getBitCoinGraphInfoUseCase)
        bitCoinPriceGraphViewModel.loadBitCoinGraphModel()
        bitCoinPriceGraphViewModel.getLoadingStatusLiveData().observeOnce {
            assertEquals(ERROR_MSG_SOME_THING_WENT_WRONG, it.message)
        }
    }

    companion object {
        private const val ERROR_MSG_NO_INTERNET = "Connect to internet and try again"
        private const val ERROR_VALIDATION = "Validation failed"
        private const val ERROR_MSG_SOME_THING_WENT_WRONG = "Something went wrong!"

        private const val RECOMMENDED_RELOAD_TEXT =
            "Provided price might be outdated. Connect to internet to get the latest price!"
    }
}
