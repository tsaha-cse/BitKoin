package com.tushar.module.domain.base

import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

open class BaseTest {
    @Rule
    @JvmField
    var mockitoRule: MockitoRule? = MockitoJUnit.rule()
}
