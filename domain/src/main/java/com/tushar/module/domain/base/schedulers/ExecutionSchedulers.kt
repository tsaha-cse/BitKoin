package com.tushar.module.domain.base.schedulers

import io.reactivex.Scheduler

interface ExecutionSchedulers {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}
