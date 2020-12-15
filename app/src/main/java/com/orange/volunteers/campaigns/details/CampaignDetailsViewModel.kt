package com.orange.volunteers.campaigns.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.UserEnrollmentModel
import com.orange.domain.model.UserEnrollmentStatus
import com.orange.domain.model.UserEnrollmentsModel
import com.orange.domain.usecases.EnrollToCampaignUseCase
import com.orange.domain.usecases.IsEnrolledToCampaignUseCase
import com.orange.domain.usecases.UnSubscribeFromCampaignUseCase
import com.orange.volunteers.history.YearlyEnrollments
import com.orange.volunteers.instances.Instances
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class CampaignDetailsViewModel (
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val enrollToCampaignUseCase: EnrollToCampaignUseCase,
    private val unSubscribeFromCampaignUseCase: UnSubscribeFromCampaignUseCase,
    private val isEnrolledToCampaignUseCase: IsEnrolledToCampaignUseCase
) : ViewModel() {
    val data = MutableLiveData<List<CampaignModel>>()
    val showError = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val isUserEnrolled = MutableLiveData<Boolean>()
    private val enrollService by lazy {
        Instances.servicesProvider.getEnrollService()
    }
    val enrollmentID = MutableLiveData<String>()
    private val disposable: CompositeDisposable = CompositeDisposable()

    fun enrollToCampaign(campaignId: String) {
        enrollService.enrollToCampaign(campaignId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    isUserEnrolled.value = true
                    showError.value = false
                },
                {
                    isUserEnrolled.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun cancelEnrollment(enrollmentId: String) {
        enrollService.cancelEnrollment(enrollmentId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    isUserEnrolled.value = false
                    showError.value = false
                },
                {
                    isUserEnrolled.value = true
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun isUserEnrolled(campaignId: String) {
        isLoading.value = true
        enrollService.getEnrollments(isActive = true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val result = it.content?.filter { userEnrollment ->
                        userEnrollment.campaign.uuid == campaignId
                    }
                    if (result != null) {
                        for(enrollment in result) {
                            when(enrollment.status) {
                                UserEnrollmentStatus.CANCELLED, UserEnrollmentStatus.REJECTED -> {
                                    isUserEnrolled.value = false
                                }
                                else -> {
                                    isUserEnrolled.value = true
                                }
                            }
                        }
                    } else {
                        isUserEnrolled.value = false
                    }

                    isLoading.value = false
                    showError.value = false
                },
                {
                    isUserEnrolled.value = false
                    isLoading.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun getEnrollmentId(campaignId: String) {
        enrollService.getEnrollments(isActive = true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val result = it.content?.filter {
                        it.campaign.uuid == campaignId
                    }
                    if (result != null) {
                        for(item in result) {
                            if(item.campaign.uuid == campaignId)
                            {
                                enrollmentID.value = item.uuid
                            }
                        }
                    } else {
                        Log.v("Get enrollment ID", "No enrollment found for this campaign")
                    }

                    showError.value = false

                },
                {
                    Log.e("Get enrollment ID error", "${it.message}")
                    showError.value = true
                }
            ).addTo(disposable)
    }

}