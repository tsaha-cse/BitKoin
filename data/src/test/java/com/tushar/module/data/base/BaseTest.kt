package com.tushar.module.data.base

import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

open class BaseTest {
    @Rule
    @JvmField
    var mockitoRule: MockitoRule? = MockitoJUnit.rule()
}