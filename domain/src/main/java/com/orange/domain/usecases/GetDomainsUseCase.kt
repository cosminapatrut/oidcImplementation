package com.orange.domain.usecases

import com.orange.domain.model.CampaignModel
import com.orange.domain.model.DomainModel
import com.orange.domain.repository.CampaignsRepository
import com.orange.domain.repository.DomainsRepository
import io.reactivex.Single

class GetDomainsUseCaseImpl(
    private val domainsRepository: DomainsRepository
) : GetDomainsUseCase {
    override fun getDomains(): Single<List<DomainModel>> {
        return domainsRepository.getDomains()
            .toObservable()
            .flatMapIterable { it }
//            .filter { it.objectID != null }
            .toList()
    }
}

interface GetDomainsUseCase {
    fun getDomains(): Single<List<DomainModel>>
}