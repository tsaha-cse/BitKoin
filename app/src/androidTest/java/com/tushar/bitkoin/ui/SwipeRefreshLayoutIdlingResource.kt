package com.tushar.bitkoin.ui

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.IdlingResource

class SwipeRefreshLayoutIdlingResource(
    private val view: SwipeRefreshLayout,
    private val isRefreshing: Boolean
) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private var isIdle: Boolean = false

    override fun getName(): String {
        return SwipeRefreshLayoutIdlingResource::class.java.name + ":" + view.id + ":" + isRefreshing
    }

    override fun isIdleNow(): Boolean {
        if (isIdle) return true

        isIdle = view.isRefreshing == isRefreshing

        if (isIdle) {
            resourceCallback?.onTransitionToIdle()
        }

        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}