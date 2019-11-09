package com.tushar.bitkoin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tushar.bitkoin.BuildConfig
import com.tushar.bitkoin.R
import com.tushar.module.data.api.BitCoinGraphInfoDeserializer
import com.tushar.module.data.api.BlockChainApi
import com.tushar.module.data.datasource.local.BitCoinGraphLocalStorage
import com.tushar.module.data.datasource.local.BitCoinGraphSharedPreferenceDataLocalStorage
import com.tushar.module.data.datasource.remotee.BitCoinGraphNetworkDataSource
import com.tushar.module.data.datasource.remotee.BitCoinGraphNetworkDataSourceImpl
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.repository.BitCoinGraphRepository
import com.tushar.module.data.repository.BitCoinGraphRepositoryImpl
import com.tushar.module.data.util.NoConnectionInterceptor
import com.tushar.module.data.util.createOkHttp
import com.tushar.module.data.util.createWebService
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@ClassOpen
@Module
class NetworkModule {

    val baseUrl: String
        get() = BuildConfig.BLOCK_CHAIN_API_BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(BitCoinGraphModel::class.java, BitCoinGraphInfoDeserializer())
            .create()

    @Provides
    @Singleton
    fun provideNoConnectionInterceptor(context: Context): NoConnectionInterceptor =
        NoConnectionInterceptor(context)

    @Provides
    @Singleton
    @Named("base_url")
    fun provideBaseUrl(): String = baseUrl

    @Provides
    @Singleton
    fun provideBlockChainApiService(
        @Named("base_url") baseUrl: String,
        gson: Gson,
        noConnectionInterceptor: NoConnectionInterceptor
    ): BlockChainApi =
        createWebService(
            baseUrl,
            createOkHttp(
                interceptors = listOf(noConnectionInterceptor),
                level =
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            ),
            setOf(GsonConverterFactory.create(gson))
        )

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideBitCoinGraphDataSource(blockChainApi: BlockChainApi): BitCoinGraphNetworkDataSource =
        BitCoinGraphNetworkDataSourceImpl(blockChainApi)

    @Provides
    @Singleton
    fun provideBitCoinGraphStorage(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): BitCoinGraphLocalStorage =
        BitCoinGraphSharedPreferenceDataLocalStorage(
            sharedPreferences,
            gson
        )

    @Provides
    @Singleton
    fun provideBitCoinGraphRepository(
        bitCoinGraphNetworkDataSource: BitCoinGraphNetworkDataSource,
        bitCoinGraphLocalStorage: BitCoinGraphLocalStorage
    ): BitCoinGraphRepository =
        BitCoinGraphRepositoryImpl(bitCoinGraphNetworkDataSource, bitCoinGraphLocalStorage)

}
