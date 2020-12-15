package com.orange.data.service.campaign

import com.orange.data.service.campaign.CampaignsItemDTO
import io.reactivex.Single
import retrofit2.http.GET

interface CampaignsService {

    @GET("campaign")
//    @GET("cosminapatrut/volunteersApi/master/mock_campaigns.json")
    fun getCampaigns(): Single<CampaignsDTO>

//    @GET("cosminapatrut/artGalleryApi/master/{artworkId}.json")
//    fun getGalleryDetail(
//        @Path("artworkId")
//        id: Long
//    ): Single<CampaignItemDetailDTO>

}