package com.tushar.bitkoin.di

import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import com.tushar.module.domain.base.schedulers.TestAppSchedulers
import dagger.Module

@Module
class TestDomainModule : DomainModule() {

    override val executionSchedulers: ExecutionSchedulers
        get() = TestAppSchedulers()
}
