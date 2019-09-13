package com.tushar.bitkoin.di

import com.tushar.module.domain.base.schedulers.AppSchedulers
import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * use cases dependencies resolved here
 */
@Module
object DomainModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideExecutionSchedulers(): ExecutionSchedulers = AppSchedulers()
}