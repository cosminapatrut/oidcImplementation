package com.orange.volunteers.history.pastCampaigns

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.UserEnrollmentModel
import com.orange.domain.model.UserEnrollmentsModel
import com.orange.domain.usecases.GetCampaignsUseCase
import com.orange.domain.usecases.GetPastCampaignsUseCase
import com.orange.volunteers.history.YearlyCampaigns
import com.orange.volunteers.history.YearlyEnrollments
import com.orange.volunteers.instances.Instances
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HistoryPastCampaignsViewModel(
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val getPastCampaignsUseCase: GetPastCampaignsUseCase
) : ViewModel() {
    val data = MutableLiveData<List<CampaignModel>>()

    private val disposable: CompositeDisposable = CompositeDisposable()
    val isLoading = MutableLiveData<Boolean>(false)
    val showError = MutableLiveData<Boolean>()
    val enrollments = MutableLiveData<List<YearlyEnrollments>>()
    private val enrollService by lazy {
        Instances.servicesProvider.getEnrollService()
    }

    fun getEnrollments() {
        isLoading.value = true
        enrollService.getEnrollments()
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

}