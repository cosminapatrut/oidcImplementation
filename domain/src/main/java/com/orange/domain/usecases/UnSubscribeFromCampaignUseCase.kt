package com.orange.domain.usecases

import com.orange.domain.repository.EnrollmentRepository
import io.reactivex.Completable

class UnSubscribeFromCampaignUseCaseImpl (
    private val enrollmentRepository: EnrollmentRepository
) : UnSubscribeFromCampaignUseCase {
    override fun unSubscribeFromCampaign(campaignId: String, bearer: String): Completable {
        return Completable.fromCallable{
            enrollmentRepository.unSubscribeFromCampaign(campaignId, bearer)
        }
    }
}

interface UnSubscribeFromCampaignUseCase {
    fun unSubscribeFromCampaign(campaignId: String, bearer: String): Completable
}