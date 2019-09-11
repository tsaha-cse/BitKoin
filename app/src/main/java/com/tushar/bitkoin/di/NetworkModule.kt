package com.tushar.bitkoin.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tushar.bitkoin.BuildConfig
import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.util.createOkHttp
import com.tushar.module.data.util.createWebService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @JvmStatic
    @Provides
    @Singleton
    fun provideBlockChainApiService(gson: Gson): BlockChainApi =
        createWebService<BlockChainApi>(
            BuildConfig.BLOCK_CHAIN_API_BASE_URL,
            createOkHttp(
                level =
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            ),
            setOf(GsonConverterFactory.create(gson))
        )
}