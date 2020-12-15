package com.orange.data.service.enrollment

import com.orange.data.service.campaign.CampaignsItemDTO
import io.reactivex.Completable
import retrofit2.http.*

interface EnrollmentService {
    @POST("enrollment/{campaignUuid}")
    fun enrollToCampaign(@Path("campaignUuid") campaignId: String): Completable

    @DELETE("enrollment/{campaignUuid}/cancel")
    fun unSubscribeFromCampaign(@Header("Authorization") bearer: String,@Path("campaignUuid")
                         campaignId: String): Completable

    @GET("enrollment")
    fun getEnrollments(@Header("Authorization") bearer: String): List<CampaignsItemDTO>
}