package com.orange.domain.usecases

import com.orange.domain.model.CampaignModel
import com.orange.domain.repository.EnrollmentRepository
import io.reactivex.Completable
import io.reactivex.Single

class EnrollToCampaignUseCaseImpl (
    private val enrollmentRepository: EnrollmentRepository
) : EnrollToCampaignUseCase {
    override fun enrollToCampaign(campaignId: String): Completable {
        return Completable.fromCallable {
            enrollmentRepository.enrollToCampaign(campaignId)
        }
    }
}

interface EnrollToCampaignUseCase {
    fun enrollToCampaign(campaignId: String): Completable
}