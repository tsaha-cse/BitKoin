package com.tushar.module.domain.base.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestAppSchedulers : ExecutionSchedulers {
    override fun io(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}
