package com.tushar.module.data.datasource.local

import android.content.SharedPreferences
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tushar.module.data.TIME_SPAN_1_YEAR
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.base.BaseTest
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.price1YearJson
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock

class BitCoinGraphSharedPreferenceDataLocalStorageTest : BaseTest() {

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphModel::class.java, BitCoinGraphInfoDeserializer())
            .create()

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var bitCoinGraphSharedPreferenceDataLocalStorage: BitCoinGraphSharedPreferenceDataLocalStorage

    @Before
    fun setUp() {
        bitCoinGraphSharedPreferenceDataLocalStorage =
            BitCoinGraphSharedPreferenceDataLocalStorage(sharedPreferences, gson)
    }

    @Test
    fun `Should return from storage`() {
        bitCoinGraphSharedPreferenceDataLocalStorage =
            BitCoinGraphSharedPreferenceDataLocalStorage(sharedPreferences, gson)
        val bitCoinGraphModel: BitCoinGraphModel = gson.fromJson(price1YearJson)
        whenever(sharedPreferences.getString(anyString(), anyOrNull())).thenReturn(
            gson.toJson(
                BitCoinGraphLocalModel(TIME_SPAN_1_YEAR, bitCoinGraphModel)
            )
        )
        bitCoinGraphSharedPreferenceDataLocalStorage.getGraphInfo(TIME_SPAN_1_YEAR)
            .test()
            .assertValue(Pair(TIME_SPAN_1_YEAR, bitCoinGraphModel))
    }

    @Test
    fun `Should not return from storage and throws SharedPreferenceStorageException`() {
        whenever(sharedPreferences.getString(anyString(), anyOrNull())).thenReturn(null)
        bitCoinGraphSharedPreferenceDataLocalStorage.getGraphInfo(TIME_SPAN_1_YEAR)
            .test()
            .assertError(SharedPreferenceStorageException::class.java)
    }

    @Test
    fun `Should not return from storage and throws SharedPreferenceStorageException because of invalid data`() {
        whenever(sharedPreferences.getString(anyString(), anyOrNull())).thenReturn("{}")
        bitCoinGraphSharedPreferenceDataLocalStorage.getGraphInfo(TIME_SPAN_1_YEAR)
            .test()
            .assertError(SharedPreferenceStorageException::class.java)
    }

    @Test
    fun `Should save graph info passed by proper value`() {
        val bitCoinGraphModel: BitCoinGraphModel = gson.fromJson(price1YearJson)
        val editor: SharedPreferences.Editor = mock()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        val sharedPreferenceKeyCaptor = argumentCaptor<String>()
        val sharedPreferenceValueCaptor = argumentCaptor<String>()

        bitCoinGraphSharedPreferenceDataLocalStorage.saveGraphInfo(
            TIME_SPAN_1_YEAR,
            bitCoinGraphModel
        ).test()
        verify(sharedPreferences.edit()).putString(
            sharedPreferenceKeyCaptor.capture(),
            sharedPreferenceValueCaptor.capture()
        )

        assertEquals(BIT_COIN_GRAPH_MODEL_KEY, sharedPreferenceKeyCaptor.firstValue)
        assertEquals(
            gson.toJson(BitCoinGraphLocalModel(TIME_SPAN_1_YEAR, bitCoinGraphModel)),
            sharedPreferenceValueCaptor.firstValue
        )
    }

    companion object {
        private const val BIT_COIN_GRAPH_MODEL_KEY = "bit_coin_graph_model"
    }

}
