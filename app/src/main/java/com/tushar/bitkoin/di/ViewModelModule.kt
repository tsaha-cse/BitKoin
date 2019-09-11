package com.tushar.bitkoin.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tushar.module.presentation.GraphViewModel
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
    @ViewModelKey(GraphViewModel::class)
    abstract fun bindGraphViewModel(graphViewModel: GraphViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: BitKoinViewModelFactory): ViewModelProvider.Factory
}