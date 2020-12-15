package com.orange.domain.model

open class CampaignModel(
    val uuid: String? = null,
    val pictures: List<CampaignPicturesModel> = emptyList(),
    val name: String? = null,
    val fullDescription: String? = null,
    val shortDescription: String? = null,
    val startDate: String,
    val endDate: String,
    val address: CampaignAddress? = null,
    val occupancy: Int? = null,
    val capacity: Int? = null)