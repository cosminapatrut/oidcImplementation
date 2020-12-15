package com.orange.volunteers.history.activeCampaigns

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.UserEnrollmentStatus
import com.orange.domain.usecases.GetActiveCampaignsUseCase
import com.orange.domain.usecases.GetPastCampaignsUseCase
import com.orange.volunteers.history.YearlyEnrollments
import com.orange.volunteers.instances.Instances
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class HistoryActiveCampaignsViewModel (
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val getActiveCampaignsUseCase: GetActiveCampaignsUseCase
) : ViewModel() {
    private val disposable: CompositeDisposable = CompositeDisposable()
    val data = MutableLiveData<List<CampaignModel>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val showError = MutableLiveData<Boolean>(false)
    val isUserEnrolled = MutableLiveData<Boolean>()
    val enrollments = MutableLiveData<List<YearlyEnrollments>>()
    val enrollmentID = MutableLiveData<String>()

    private val enrollService by lazy {
        Instances.servicesProvider.getEnrollService()
    }

    fun getEnrollments() {
        isLoading.value = true
        enrollService.getEnrollments(isActive = true)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val format = SimpleDateFormat("yyyy-M-dd")
                    val calendar = Calendar.getInstance()

                    val enrollmetsList = arrayListOf<YearlyEnrollments>()
                    it.content?.groupBy { enrollment ->
                        val date = format.parse(enrollment.campaign.endDate)
                        calendar.time = date
                        calendar.get(Calendar.YEAR)
                    }?.map { enrollment ->
                        val year = enrollment.key
                        val nrOfEnrollments = enrollment.value.size
                        val list = enrollment.value
                        enrollmetsList.add(YearlyEnrollments(year, nrOfEnrollments, list))
                    }

                    isLoading.value = false
                    showError.value = false
                    enrollments.postValue(enrollmetsList)

                },
                {
                    isLoading.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun isUserEnrolled(campaignId: String) {
        enrollService.getEnrollments()
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
//                            isUserEnrolled.value = enrollment.status != UserEnrollmentStatus.CANCELLED
                        }
                    } else {
                        isUserEnrolled.value = false
                    }
                },
                {
                    isUserEnrolled.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun getEnrollmentId(campaignId: String) {
        enrollService.getEnrollments()
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
                },
                {
                    Log.e("Get enrollment ID error", "${it.message}")
                    showError.value = true
                }
            ).addTo(disposable)
    }

}
  