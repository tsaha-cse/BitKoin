package com.tushar.module.domain.base

/**
 * Wrapper for representing an Output from API End points.
 * Ensuring the generic output type
 */
sealed class Result<out T> {

    class Success<T>(val value: T) : Result<T>()

    class Failure<T>(val reason: Reason) : Result<T>()

    class Reason(val code: String? = null, val exception: Exception)
}
