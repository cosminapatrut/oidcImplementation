package com.orange.data.defaults

import com.orange.data.service.enrollment.EnrollmentService
import com.orange.domain.model.CampaignModel
import com.orange.domain.repository.EnrollmentRepository

class EnrollmentRepositoryImpl(private val enrollmentService: EnrollmentService) : EnrollmentRepository {

    override fun enrollToCampaign(campaignId: String) {
        enrollmentService.enrollToCampaign(
            campaignId
        )


    }

    override fun unSubscribeFromCampaign(campaignId: String, bearer: String) {
        enrollmentService.unSubscribeFromCampaign(
            campaignId,
            bearer
        )
    }

    override fun getEnrollments(bearer: String): List<CampaignModel> {
        return enrollmentService.getEnrollments(bearer)
            .map {campaignItemDTO ->
                CampaignModel(
                    uuid = campaignItemDTO.uuid,
                    pictures = campaignItemDTO.images,
                    name = campaignItemDTO.name,
                    fullDescription = campaignItemDTO.fullDescription,
                    startDate = campaignItemDTO.startDate,
                    endDate = campaignItemDTO.endDate,
                    address = campaignItemDTO.address,
                    occupancy = campaignItemDTO.enrolledUserCount,
                    capacity = campaignItemDTO.capacity
                )
            }

    }

}