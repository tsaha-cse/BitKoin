package com.tushar.bitkoin.di

import android.app.Application
import com.tushar.bitkoin.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuildersModule::class,
        TestNetworkModule::class,
        TestDomainModule::class
    ]
)
interface TestAppComponent {
    fun inject(app: App)

    @Component.Builder
    interface Builder {
        // this exposed method is needed for DaggerMock
        fun testNetworkModule(testNetworkModule: TestNetworkModule): Builder

        fun testDomainModule(testDomainModule: TestDomainModule): Builder

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): TestAppComponent
    }
}
