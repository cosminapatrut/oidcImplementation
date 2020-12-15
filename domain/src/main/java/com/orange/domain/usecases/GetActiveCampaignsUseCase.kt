package com.orange.domain.usecases

import com.orange.domain.model.CampaignModel
import com.orange.domain.repository.CampaignsRepository
import io.reactivex.Single
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class GetActiveCampaignsUseCaseImpl (
private val galleryRepository: CampaignsRepository
) : GetActiveCampaignsUseCase {

    override fun getActiveCampaigns(): Single<List<CampaignModel>> {
        return galleryRepository.getCampaigns()
            .toObservable()
            .flatMapIterable { it }
//            .filter { item ->
//                //For testing get only campaigns >= 2020
//                val  calendar = Calendar.getInstance()
//                calendar.time = item.endDate?.formatStringToDate()
//                val yearOfItem = calendar.get(Calendar.YEAR)
//                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//
//                yearOfItem >= currentYear
//            }
            .toList()
    }

    fun String.formatStringToDate(): Date? {
        val parser = SimpleDateFormat("yyyy-M-dd")
        try {
            val endDateTruncated = this.substringBefore(" ")
            return parser.parse(endDateTruncated)
        } catch (ex: Exception) {
            return Date()
        }
    }
}

interface GetActiveCampaignsUseCase {
    fun getActiveCampaigns(): Single<List<CampaignModel>>
}

