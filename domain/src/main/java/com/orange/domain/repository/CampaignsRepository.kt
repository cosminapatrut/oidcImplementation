package com.orange.domain.repository

import com.orange.domain.model.CampaignModel
import io.reactivex.Single

interface CampaignsRepository {
    fun getCampaigns(): Single<List<CampaignModel>>
}