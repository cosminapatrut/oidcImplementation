package com.orange.volunteers.campaigns.details

import android.content.Context
import com.orange.domain.model.UserEnrollmentsModel
import io.reactivex.Completable
import io.reactivex.Single

class EnrollmentServiceImpl(val context: Context) : EnrollService {

    private val enrollmentClient by lazy {
        EnrollmentClient.create()
    }

    override fun enrollToCampaign(campaignId: String): Completable =
        enrollmentClient.enrollToCampaign(campaignId)

    override fun cancelEnrollment(enrollmentId: String): Completable =
        enrollmentClient.cancelEnrollment(enrollmentId)


    override fun getEnrollments(isActive: Boolean): Single<UserEnrollmentsModel> {
        return enrollmentClient.getEnrollments(isActive)
            .map { enrollments: UserEnrollmentsModel ->  enrollments }
    }


}
