package com.tushar.bitkoin.util

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tushar.bitkoin.App
import com.tushar.bitkoin.di.DomainModule
import com.tushar.bitkoin.di.NetworkModule
import com.tushar.bitkoin.di.TestAppComponent
import com.tushar.bitkoin.di.TestDomainModule
import com.tushar.bitkoin.di.TestNetworkModule
import it.cosenonjaviste.daggermock.DaggerMock
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

class TestSetup {

    fun getRule(
        target: Any,
        activityTestRule: ActivityTestRule<*>,
        domainModule: DomainModule = TestDomainModule(),
        networkModule: NetworkModule = TestNetworkModule(URL, PORT)
    ): TestRule = RuleChain.emptyRuleChain()
        .around { base, _ ->
            DaggerMock.rule<TestAppComponent>(
                domainModule,
                networkModule
            ).set { it.inject(getApp()) }
                .customizeBuilder<TestAppComponent.Builder> { it.application(getApp()) }
                .apply(base, null, target)
        }
        .around(activityTestRule)
        .around(ClearPreferencesRule())

    companion object {
        fun getApp() =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    }
}