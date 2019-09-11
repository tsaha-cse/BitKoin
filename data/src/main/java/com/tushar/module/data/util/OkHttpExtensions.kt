package com.tushar.module.data.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * to create webservice for retrofit
 */
inline fun <reified T> createWebService(
    url: String,
    okHttpClient: OkHttpClient,
    converters: Set<Converter.Factory>
): T =
    Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient).also { builder ->
            converters.forEach {
                builder.addConverterFactory(it)
            }
        }
        .build().create(T::class.java)

/**
 * to create OkHttp client with the interceptor passing
 * into the list
 */
fun createOkHttp(
    interceptors: List<Interceptor> = emptyList(),
    level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
    timeoutSeconds: Long = 30L
): OkHttpClient =
    OkHttpClient.Builder()
        .apply {
            readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            interceptors.forEach { addInterceptor(it) }
            addInterceptor(HttpLoggingInterceptor().setLevel(level))
        }.build()