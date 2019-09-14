package com.tushar.module.presentation.viewmodel

class UILoadingState(
    val showLoader: Boolean = false,
    val message: String? = null,
    val retryRecommendation: RetryRecommendation = RetryRecommendation.NOT_NEEDED
)

sealed class RetryRecommendation {
    object NOT_NEEDED : RetryRecommendation()
    object OPTIONAL : RetryRecommendation()
    object MUST : RetryRecommendation()
}
