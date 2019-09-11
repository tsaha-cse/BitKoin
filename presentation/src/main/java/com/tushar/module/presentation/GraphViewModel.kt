package com.tushar.module.presentation

import androidx.lifecycle.ViewModel
import timber.log.Timber
import javax.inject.Inject

class GraphViewModel @Inject constructor() : ViewModel() {

    fun onResponse() {
        Timber.tag("GraphViewModel").d("Yahoo! it's working!")
    }
}