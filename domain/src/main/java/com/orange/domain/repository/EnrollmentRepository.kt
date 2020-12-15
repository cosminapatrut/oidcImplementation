package com.orange.domain.repository

import com.orange.domain.model.CampaignModel
import io.reactivex.Completable
import io.reactivex.Single

interface EnrollmentRepository {
    fun enrollToCampaign(campaignId: String)
    fun unSubscribeFromCampaign(campaignId: String, bearer: String)
    fun getEnrollments(bearer: String): List<CampaignModel>
}