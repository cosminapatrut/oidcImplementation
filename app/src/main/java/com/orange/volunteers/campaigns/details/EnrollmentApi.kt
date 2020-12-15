package com.orange.volunteers.campaigns.details

import com.orange.data.service.campaign.CampaignsItemDTO
import com.orange.domain.model.UserEnrollmentModel
import com.orange.domain.model.UserEnrollmentsModel
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface EnrollmentApi {
    @POST("enrollment/{campaignUuid}")
    fun enrollToCampaign(@Path("campaignUuid") campaignId: String): Completable

    //TODO
    @DELETE("enrollment/{enrollmentId}/cancel")
    fun cancelEnrollment(@Path("enrollmentId") enrollmentId: String): Completable

    //TODO
    @GET("enrollment")
    fun getEnrollments(@Query("isActive") isActive: Boolean = false): Single<UserEnrollmentsModel>
}