package com.orange.data.defaults

import com.orange.data.service.campaign.CampaignsService
import com.orange.domain.model.CampaignModel
import com.orange.domain.repository.CampaignsRepository
import io.reactivex.Single

class CampaignsRepositoryImpl(private val service: CampaignsService) : CampaignsRepository {
    override fun getCampaigns(): Single<List<CampaignModel>> {
        return service.getCampaigns()
            .toObservable()
            .flatMapIterable { it.content }
            .map { campaignItemDTO ->
                CampaignModel(
                    uuid = campaignItemDTO.uuid,
                    pictures = campaignItemDTO.images,
                    name = campaignItemDTO.name,
                    fullDescription = campaignItemDTO.fullDescription,
                    shortDescription = campaignItemDTO.shortDescription,
                    startDate = campaignItemDTO.startDate,
                    endDate = campaignItemDTO.endDate,
                    address = campaignItemDTO.address,
                    occupancy = campaignItemDTO.enrolledUserCount,
                    capacity = campaignItemDTO.capacity
                )
            }
            .toList()
    }
}
