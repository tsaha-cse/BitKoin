package com.tushar.bitkoin.di

import com.tushar.module.domain.base.schedulers.AppSchedulers
import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * use cases dependencies resolved here
 */
@Mockable
@Module
class DomainModule {

    val executionSchedulers: ExecutionSchedulers
        get() = AppSchedulers()

    @Provides
    @Singleton
    fun provideExecutionSchedulers(): ExecutionSchedulers = AppSchedulers()
}