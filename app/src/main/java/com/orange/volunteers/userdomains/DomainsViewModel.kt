package com.orange.volunteers.userdomains

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UserResponseModel
import com.orange.domain.usecases.*
import io.reactivex.Flowable.fromIterable
import io.reactivex.Observable.fromIterable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*

class DomainsViewModel (
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getDomainsUseCase: GetDomainsUseCase,
    private val getInterestedDomainsUseCase: GetInterestedDomainsUseCase
) : ViewModel() {
    val data = MutableLiveData<List<DomainModel>>()
    val userData = MutableLiveData<UserResponseModel>()
    var interestedDomains =  MutableLiveData<List<DomainModel>>()
    val isLoading = MutableLiveData<Boolean>()
    val showError = MutableLiveData<Boolean>(false)
    private val disposable: CompositeDisposable = CompositeDisposable()

    fun getActivityDomains() {
//        isLoading.value = true
        getDomainsUseCase.getDomains()
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .doFinally {

            }
            .subscribe(
                { responseItems ->
                    data.postValue(responseItems)
//                    isLoading.value = false
                    showError.value = false

                },
                {
                    showError.value = true
//                    isLoading.value = false
                }
            )
            .addTo(disposable)
    }

    fun getUserInfo() {
//        isLoading.value = true
        getUserInfoUseCase.getUserInfo()
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .subscribe(
                { responseItems ->
                    userData.postValue(responseItems)
//                    isLoading.value = false
                    showError.value = false

                },
                {
                    showError.value = true

                    Log.e("UserInfo", " error")
//                    isLoading.value = false
                }
            ).addTo(disposable)
    }

    fun getInterestedDomains(): List<DomainModel> {
        return getInterestedDomainsUseCase.getInterestedDomains()
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .blockingGet()
    }

    fun getList() {

    }


}