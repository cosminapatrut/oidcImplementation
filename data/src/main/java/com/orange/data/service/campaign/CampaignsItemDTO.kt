package com.orange.data.service.campaign

import android.location.Address
import com.google.gson.annotations.SerializedName
import com.orange.domain.model.CampaignAddress
import com.orange.domain.model.CampaignImagesModel
import com.orange.domain.model.CampaignPicturesModel
import java.util.*

data class CampaignsItemDTO(
    val uuid: String?,
    @SerializedName("pictures")
    val images: List<CampaignPicturesModel>,
    val name: String?,
    val fullDescription: String?,
    val shortDescription: String?,
    val startDate: String,
    val endDate: String,
    val address: CampaignAddress?,
    @SerializedName("occupancy")
    val enrolledUserCount: Int?,
    val capacity: Int?
)

