package com.orange.volunteers.campaigns.details

import com.orange.domain.model.UserEnrollmentsModel
import io.reactivex.Completable
import io.reactivex.Single

interface EnrollService {
    fun enrollToCampaign(campaignId: String): Completable
    fun cancelEnrollment(enrollmentId: String): Completable
    fun getEnrollments(isActive: Boolean = false): Single<UserEnrollmentsModel>
}