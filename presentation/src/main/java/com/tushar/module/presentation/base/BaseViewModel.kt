package com.tushar.module.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected fun <T> LiveData<T>.postVal(value: T) {
        (this as? MutableLiveData)?.postValue(value)
    }
}