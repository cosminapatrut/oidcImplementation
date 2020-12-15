package com.orange.domain.repository

import com.orange.domain.model.CampaignModel
import com.orange.domain.model.DomainModel
import io.reactivex.Single

interface DomainsRepository {
    fun getDomains(): Single<List<DomainModel>>
}