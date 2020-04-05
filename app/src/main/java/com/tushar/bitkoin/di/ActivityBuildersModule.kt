package com.tushar.bitkoin.di

import com.tushar.bitkoin.ui.BitCoinPriceGraphActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract fun bindGraphActivity(): BitCoinPriceGraphActivity
}
