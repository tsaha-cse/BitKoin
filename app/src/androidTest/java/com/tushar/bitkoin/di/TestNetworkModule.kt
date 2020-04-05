package com.tushar.bitkoin.di

import dagger.Module

@Module
class TestNetworkModule(
    private val url: String,
    private val port: Int
) : NetworkModule() {
    override val baseUrl: String
        get() = "$url:$port"
}
