package com.orange.domain.usecases

import android.util.Log
import com.orange.domain.model.CampaignModel
import com.orange.domain.repository.CampaignsRepository
import io.reactivex.Single

class GetCampaignsUseCaseImpl(
    private val campaignsRepository: CampaignsRepository
) : GetCampaignsUseCase {
    override fun getCampaigns(): Single<List<CampaignModel>> {
        return campaignsRepository.getCampaigns()
            .toObservable()
            .flatMapIterable { it }
//            .filter { it.objectID != null }
            .toList()
            .doOnError {
                Log.e("Error", "$it")
            }
    }
}

interface GetCampaignsUseCase {
    fun getCampaigns(): Single<List<CampaignModel>>
}