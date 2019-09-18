package com.tushar.module.domain.usecase

import com.tushar.module.data.repository.BitCoinGraphInfo
import com.tushar.module.data.repository.BitCoinGraphRepository
import com.tushar.module.domain.base.NullParamException
import com.tushar.module.domain.base.ParamValidationException
import com.tushar.module.domain.base.RxSingleUseCase
import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class GetBitCoinGraphInfoUseCase
@Inject constructor(
    private val bitCoinGraphRepository: BitCoinGraphRepository,
    executionSchedulers: ExecutionSchedulers
) : RxSingleUseCase<TimeSpan, BitCoinGraphInfo>(executionSchedulers) {

    override fun validate(param: TimeSpan?): Completable = param?.let {
        when {
            it.count <= 0 -> Completable.error(ParamValidationException("TimeSpan.count<=0 is not allowed!"))
            else -> Completable.complete()
        }
    } ?: Completable.error(ParamValidationException("the param TimeSpan is null"))

    override fun buildUseCase(param: TimeSpan?): Single<BitCoinGraphInfo> =
        param?.let {
            bitCoinGraphRepository.getGraphInfo("${param.count}${param.timeUnit.key}")
        } ?: Single.error(NullParamException())
}

data class TimeSpan(val count: Int, val timeUnit: TimeUnit)

sealed class TimeUnit(val key: String) {
    object Day : TimeUnit("days")
    object Week : TimeUnit("weeks")
    object Months : TimeUnit("months")
    object Year : TimeUnit("years")
}