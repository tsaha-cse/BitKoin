package com.tushar.module.presentation.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tushar.module.data.model.BitCoinGraphModel
import com.tushar.module.data.repository.BitCoinGraphInfo
import com.tushar.module.data.repository.DataSource
import com.tushar.module.data.repository.NoContentException
import com.tushar.module.domain.base.NullParamException
import com.tushar.module.domain.base.ParamValidationException
import com.tushar.module.domain.usecase.GetBitCoinGraphInfoUseCase
import com.tushar.module.domain.usecase.TimeSpan
import com.tushar.module.domain.usecase.TimeSpanUnit
import com.tushar.module.presentation.base.BaseViewModel
import com.tushar.module.presentation.util.exhaustive
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class BitCoinPriceGraphViewModel
@Inject constructor(
    private val getBitCoinGraphInfoUseCase: GetBitCoinGraphInfoUseCase
) : BaseViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var recentRequestedTimeSpan: TimeSpan = TimeSpan(1, TimeSpanUnit.Week)

    fun loadBitCoinGraphModelOnLaunch() {
        loadBitCoinGraphModel()
    }

    fun loadBitCoinGraphModelOnRefresh() {
        loadBitCoinGraphModel(recentRequestedTimeSpan)
    }

    fun onClickOneWeekTimeSpan() {
        recentRequestedTimeSpan = TimeSpan(1, TimeSpanUnit.Week)
        loadBitCoinGraphModel()
    }

    fun onClickOneMonthTimeSpan() {
        recentRequestedTimeSpan = TimeSpan(1, TimeSpanUnit.Months)
        loadBitCoinGraphModel()
    }

    @SuppressWarnings("MagicNumber")
    fun onClickSixMonthTimeSpan() {
        recentRequestedTimeSpan = TimeSpan(6, TimeSpanUnit.Months)
        loadBitCoinGraphModel()
    }

    fun onClickOneYearTimeSpan() {
        recentRequestedTimeSpan = TimeSpan(1, TimeSpanUnit.Year)
        loadBitCoinGraphModel(recentRequestedTimeSpan)
    }


    private val uILoadingStateLiveData: LiveData<UILoadingState> =
        MutableLiveData<UILoadingState>().apply {
            value = UILoadingState()
        }

    /**
     * @return LiveData[UILoadingState] to observe the current
     * loading status by the observer (UI)
     */
    fun getLoadingStatusLiveData(): LiveData<UILoadingState> = uILoadingStateLiveData

    private val bitCoinGraphInfoLiveData: LiveData<BitCoinGraphInfo> = MutableLiveData()

    /**
     * @return LiveData[BitCoinGraphModel]
     * contains all data to setup graph in the UI
     */
    fun getBitCoinGraphModelLiveData(): LiveData<BitCoinGraphInfo> = bitCoinGraphInfoLiveData

    @VisibleForTesting
    fun loadBitCoinGraphModel(timeSpan: TimeSpan = recentRequestedTimeSpan) {
        getBitCoinGraphInfoUseCase(timeSpan)
            .doOnSubscribe {
                uILoadingStateLiveData.postVal(UILoadingState(true))
            }
            .subscribe(
                this@BitCoinPriceGraphViewModel::onLoadingSuccess,
                this@BitCoinPriceGraphViewModel::onLoadingError
            )
            .addTo(compositeDisposable)
    }

    private fun onLoadingSuccess(bitCoinGraphInfo: BitCoinGraphInfo) {
        bitCoinGraphInfoLiveData.postVal(bitCoinGraphInfo)
        when {
            bitCoinGraphInfo.dataSource == DataSource.Local -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        false,
                        RECOMMENDED_RELOAD_TEXT,
                        RetryRecommendation.OPTIONAL,
                        hideTimeSpanOptions = true
                    )
                )
            }
            else -> {
                uILoadingStateLiveData.postVal(UILoadingState(false))
            }
        }
    }

    private fun onLoadingError(throwable: Throwable) {
        when (throwable) {
            is ParamValidationException, is NullParamException -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        false,
                        ERROR_VALIDATION
                    )
                )
            }

            is NoContentException -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        showLoader = false,
                        message = ERROR_MSG_NO_INTERNET,
                        retryRecommendation = RetryRecommendation.MUST
                    )
                )
            }

            else -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        false,
                        ERROR_MSG_SOME_THING_WENT_WRONG,
                        RetryRecommendation.MUST,
                        hideTimeSpanOptions = true
                    )
                )
            }

        }.exhaustive
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {
        private const val ERROR_MSG_SOME_THING_WENT_WRONG = "Something went wrong!"
        private const val ERROR_MSG_NO_INTERNET = "Connect to internet and try again"
        private const val ERROR_VALIDATION = "Validation failed"
        private const val RECOMMENDED_RELOAD_TEXT =
            "Provided price might be outdated. Connect to internet to get the latest price!"
    }
}