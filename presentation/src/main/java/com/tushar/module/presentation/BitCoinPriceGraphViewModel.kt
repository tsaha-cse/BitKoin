package com.tushar.module.presentation

import androidx.lifecycle.ViewModel
import com.tushar.module.domain.usecase.GetBitCoinGraphInfoUseCase
import com.tushar.module.domain.usecase.TimeSpan
import com.tushar.module.domain.usecase.TimeUnit
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class BitCoinPriceGraphViewModel
@Inject constructor(private val getBitCoinGraphInfoUseCase: GetBitCoinGraphInfoUseCase) :
    ViewModel() {

    private val disposible: CompositeDisposable = CompositeDisposable()

    fun requestData() {
        getBitCoinGraphInfoUseCase(TimeSpan(1, TimeUnit.YEAR))
            .subscribe(
                {
                    Timber.tag("GraphResult").d(it.toString())
                },
                Timber::e
            ).addTo(disposible)
    }
}