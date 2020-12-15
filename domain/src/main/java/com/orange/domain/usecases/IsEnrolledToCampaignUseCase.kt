package com.orange.domain.usecases

import com.orange.domain.repository.EnrollmentRepository
import io.reactivex.Single

class IsEnrolledToCampaignUseCaseImpl(
    private val enrollmentRepository: EnrollmentRepository
) : IsEnrolledToCampaignUseCase {
    override fun isEnrolledToCampaign(campaignId: String, bearer: String): Single<Boolean> {
        return Single.fromCallable {
            enrollmentRepository.getEnrollments(bearer).find {
                it.uuid == campaignId
            } != null
        }
    }
}

interface IsEnrolledToCampaignUseCase {
    fun isEnrolledToCampaign(campaignId: String, bearer: String): Single<Boolean>
}