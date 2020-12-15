package com.orange.data.defaults

import com.orange.data.service.domainsOfInterest.DomainsService
import com.orange.domain.model.DomainModel
import com.orange.domain.repository.DomainsRepository
import io.reactivex.Single

class DomainsRepositoryImpl(private val domainsService: DomainsService) : DomainsRepository {
    override fun getDomains(): Single<List<DomainModel>> {
        return domainsService.getDomains()
            .toObservable()
            .flatMapIterable { it }
            .map { domainItemDTO ->
                DomainModel(
                    uuid = domainItemDTO.uuid,
                    name = domainItemDTO.name
                )
            }
            .toList()
    }
}