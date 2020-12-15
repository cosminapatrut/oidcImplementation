package com.orange.volunteers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class BaseViewModel(protected val app: Application) : AndroidViewModel(app) {
    protected val disposable = CompositeDisposable()

    fun io(): Scheduler = Schedulers.io()
    fun ui(): Scheduler = AndroidSchedulers.mainThread()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}