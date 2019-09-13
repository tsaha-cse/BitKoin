package com.tushar.bitkoin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tushar.bitkoin.BuildConfig
import com.tushar.bitkoin.R
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.datasource.local.BitCoinGraphDataSource
import com.tushar.module.data.datasource.local.BitCoinGraphNetworkDataSource
import com.tushar.module.data.datasource.remote.BitCoinGraphSharedPreferenceDataStorage
import com.tushar.module.data.datasource.remote.BitCoinGraphStorage
import com.tushar.module.data.model.BitCoinGraphInfo
import com.tushar.module.data.repository.BitCoinGraphRepository
import com.tushar.module.data.repository.BitCoinGraphRepositoryImpl
import com.tushar.module.data.util.createOkHttp
import com.tushar.module.data.util.createWebService
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphInfo::class.java, BitCoinGraphInfoDeserializer())
            .create()

    @JvmStatic
    @Provides
    @Singleton
    fun provideBlockChainApiService(gson: Gson): BlockChainApi =
        createWebService(
            BuildConfig.BLOCK_CHAIN_API_BASE_URL,
            createOkHttp(
                level =
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            ),
            setOf(GsonConverterFactory.create(gson))
        )

    @JvmStatic
    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    @JvmStatic
    @Provides
    @Singleton
    fun provideBitCoinGraphDataSource(blockChainApi: BlockChainApi): BitCoinGraphDataSource =
        BitCoinGraphNetworkDataSource(blockChainApi)

    @JvmStatic
    @Provides
    @Singleton
    fun provideBitCoinGraphStorage(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): BitCoinGraphStorage =
        BitCoinGraphSharedPreferenceDataStorage(
            sharedPreferences,
            gson
        )

    @JvmStatic
    @Provides
    @Singleton
    fun provideBitCoinGraphRepository(
        bitCoinGraphDataSource: BitCoinGraphDataSource,
        bitCoinGraphStorage: BitCoinGraphStorage
    ): BitCoinGraphRepository =
        BitCoinGraphRepositoryImpl(bitCoinGraphDataSource, bitCoinGraphStorage)

}