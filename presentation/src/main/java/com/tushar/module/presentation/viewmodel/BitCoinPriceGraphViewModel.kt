package com.tushar.module.presentation.viewmodel

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
import com.tushar.module.domain.usecase.TimeUnit
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

    fun loadBitCoinGraphModelOnLaunch() {
        loadBitCoinGraphModel()
    }

    fun loadBitCoinGraphModelOnRefresh() {
        loadBitCoinGraphModel()
    }

    fun onClickOneWeekTimeSpan() {
        loadBitCoinGraphModel(TimeSpan(1, TimeUnit.Week))
    }

    fun onClickOneMonthTimeSpan() {
        loadBitCoinGraphModel(TimeSpan(1, TimeUnit.Months))
    }

    fun onClickSixMonthTimeSpan() {
        loadBitCoinGraphModel(TimeSpan(6, TimeUnit.Months))
    }

    fun onClickOneYearTimeSpan() {
        loadBitCoinGraphModel(TimeSpan(1, TimeUnit.Year))
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

    private val bitCoinGraphInfoLiveData: LiveData<BitCoinGraphModel> = MutableLiveData()

    /**
     * @return LiveData[BitCoinGraphModel]
     * contains all data to setup graph in the UI
     */
    fun getBitCoinGraphModelLiveData(): LiveData<BitCoinGraphModel> = bitCoinGraphInfoLiveData

    private fun loadBitCoinGraphModel(timeSpan: TimeSpan = TimeSpan(1, TimeUnit.Year)) {
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
        bitCoinGraphInfoLiveData.postVal(bitCoinGraphInfo.bitCoinGraphModel)
        when {
            bitCoinGraphInfo.dataSource == DataSource.Local -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        false,
                        RECOMMENDED_RELOAD_TEXT,
                        RetryRecommendation.OPTIONAL
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
                        false,
                        ERROR_MSG_NO_INTERNET,
                        RetryRecommendation.MUST
                    )
                )
            }

            else -> {
                uILoadingStateLiveData.postVal(
                    UILoadingState(
                        false,
                        ERROR_MSG_SOME_THING_WENT_WRONG,
                        RetryRecommendation.MUST
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
            "Provided info might be old. Connect to internet to get the latest data!"
    }
}