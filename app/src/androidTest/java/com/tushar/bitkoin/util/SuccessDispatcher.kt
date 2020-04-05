package com.tushar.bitkoin.util

import android.content.Context
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.tushar.bitkoin.util.AssetReaderUtil.asset
import com.tushar.bitkoin.util.QUERY.QUERY_PARAM_NAME
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class SuccessDispatcher(
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
) : Dispatcher() {
    private val responseFilesByPath: Map<String, String> = mapOf(
        QUERY.QUERY_1_WEEK to MockFile.MARKET_PRICE_1_WEEK,
        QUERY.QUERY_1_MONTHS to MockFile.MARKET_PRICE_1_MONTH,
        QUERY.QUERY_6_MONTHS to MockFile.MARKET_PRICE_6_MONTHS,
        QUERY.QUERY_1_YEAR to MockFile.MARKET_PRICE_1_YEAR
    )

    override fun dispatch(request: RecordedRequest?): MockResponse {
        val errorResponse: MockResponse = MockResponse().setResponseCode(404)
        val pathWithoutQueryParams: String =
            Uri.parse(request?.path).getQueryParameter(QUERY_PARAM_NAME) ?: return errorResponse
        val responseFile: String? = responseFilesByPath[pathWithoutQueryParams]

        return if (responseFile != null) {
            val responseBody = asset(context, responseFile)
            MockResponse().setResponseCode(200).setBody(responseBody)
        } else {
            errorResponse
        }
    }
}
