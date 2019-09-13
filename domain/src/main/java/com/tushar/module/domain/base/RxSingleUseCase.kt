package com.tushar.module.domain.base

import com.tushar.module.domain.base.schedulers.ExecutionSchedulers
import io.reactivex.Completable
import io.reactivex.Single

abstract class RxSingleUseCase<Param, R>(private val schedulers: ExecutionSchedulers) {

    protected abstract fun buildUseCase(param: Param?): Single<R>

    protected open fun validate(param: Param?): Completable = Completable.complete()

    protected open fun onSuccess(result: R) {}

    protected open fun onFailure(th: Throwable) {}

    operator fun invoke(param: Param? = null): Single<R> =
        validate(param)
            .andThen(
                buildUseCase(param)
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .doOnSuccess {
                        onSuccess(it)
                    }
                    .doOnError {
                        onFailure(it)
                    }
            )
}
