package com.orange.volunteers.campaigns.overview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orange.domain.model.CampaignModel
import com.orange.domain.usecases.GetCampaignsUseCase
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class CampaignsViewModel (
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val getCampaignsUseCase: GetCampaignsUseCase
) : ViewModel() {
    val data = MutableLiveData<List<CampaignModel>>()
    val isLoading = MutableLiveData<Boolean>()
    val showError = MutableLiveData<Boolean>(false)

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun getCampaigns() {
        isLoading.value = true
        try {
            getCampaignsUseCase.getCampaigns()
                .subscribeOn(executionScheduler)
                .observeOn(postExecutionScheduler)
                .subscribe(
                    { responseItems ->
                        data.postValue(responseItems)
                        isLoading.value = false
                    },
                    {
                        showError.value = true
                        isLoading.value = false
                        Log.e("GET ", "$it")
                    }
                ).addTo(disposable)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}