package com.tushar.module.domain.usecase

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.repository.BitCoinGraphInfo
import com.tushar.module.data.repository.BitCoinGraphRepository
import com.tushar.module.data.repository.DataSource
import com.tushar.module.domain.TIME_SPAN_1_WEEK
import com.tushar.module.domain.base.BaseTest
import com.tushar.module.domain.base.NullParamException
import com.tushar.module.domain.base.ParamValidationException
import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import com.tushar.module.domain.base.schedulers.TestAppSchedulers
import com.tushar.module.domain.price1WeekJson
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class GetBitCoinGraphInfoUseCaseTest : BaseTest() {

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphModel::class.java, BitCoinGraphInfoDeserializer())
            .create()

    @Mock
    lateinit var bitCoinGraphRepository: BitCoinGraphRepository

    private val executionSchedulers: ExecutionSchedulers = TestAppSchedulers()

    private lateinit var getBitCoinGraphInfoUseCase: GetBitCoinGraphInfoUseCase

    private var timeSpanStringCaptor = argumentCaptor<String>()

    @Before
    fun setUp() {
        getBitCoinGraphInfoUseCase =
            GetBitCoinGraphInfoUseCase(bitCoinGraphRepository, executionSchedulers)
    }

    @Test
    fun `Validation should pass`() {
        getBitCoinGraphInfoUseCase.validate(TimeSpan(1, TimeUnit.Week))
            .test()
            .assertComplete()
    }

    @Test
    fun `Validation should not pass when time span is negative`() {
        getBitCoinGraphInfoUseCase.validate(TimeSpan(-1, TimeUnit.Week))
            .test()
            .assertError(ParamValidationException::class.java)
    }

    @Test
    fun `Validation should not pass when time span is null`() {
        getBitCoinGraphInfoUseCase.validate(null)
            .test()
            .assertError(ParamValidationException::class.java)
    }

    @Test
    fun `Should return BitCoinGraphInfo`() {
        val bitCoinGraphModelFor1Week: BitCoinGraphModel = gson.fromJson(price1WeekJson)
        val bitCoinGraphInfo = BitCoinGraphInfo(
            bitCoinGraphModelFor1Week,
            DataSource.Network,
            TIME_SPAN_1_WEEK
        )
        whenever(bitCoinGraphRepository.getGraphInfo(timeSpanStringCaptor.capture()))
            .thenReturn(Single.just(bitCoinGraphInfo))
        getBitCoinGraphInfoUseCase.buildUseCase(TimeSpan(1, TimeUnit.Week)).test()
            .assertValue(bitCoinGraphInfo)
        assertEquals(TIME_SPAN_1_WEEK, timeSpanStringCaptor.firstValue)
    }

    @Test
    fun `Should return NullParamException`() {
        getBitCoinGraphInfoUseCase.buildUseCase(null).test()
            .assertError(NullParamException::class.java)
    }
}