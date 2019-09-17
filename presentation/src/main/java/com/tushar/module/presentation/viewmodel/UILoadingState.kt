package com.tushar.module.presentation.viewmodel

class UILoadingState(
    val showLoader: Boolean = false,
    val message: String? = null,
    val retryRecommendation: RetryRecommendation = RetryRecommendation.NO,
    val hideTimeSpanOptions: Boolean = false
)

sealed class RetryRecommendation {
    object NO : RetryRecommendation()
    object OPTIONAL : RetryRecommendation()
    object MUST : RetryRecommendation()
}
