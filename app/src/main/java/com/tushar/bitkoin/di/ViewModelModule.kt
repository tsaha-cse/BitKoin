package com.tushar.bitkoin.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tushar.module.presentation.viewmodel.BitCoinPriceGraphViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * view model dependencies resolved here
 */

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BitCoinPriceGraphViewModel::class)
    abstract fun bindGraphViewModel(bitCoinPriceGraphViewModel: BitCoinPriceGraphViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: BitKoinViewModelFactory): ViewModelProvider.Factory
}
