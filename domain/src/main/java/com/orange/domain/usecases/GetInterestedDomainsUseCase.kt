package com.orange.domain.usecases

import com.orange.domain.model.DomainModel
import com.orange.domain.repository.UserRepository
import io.reactivex.Single

class GetInterestedDomainsUseCaseImpl(
    private val userRepository: UserRepository
) : GetInterestedDomainsUseCase {
    override fun getInterestedDomains(): Single<List<DomainModel>> {
        return userRepository.getUserInfo()
            .map {
                it.preferredDomains
            }
            .toObservable()
            .flatMapIterable {
                it
            }
            .toList()

    }
}

interface GetInterestedDomainsUseCase {
    fun getInterestedDomains(): Single<List<DomainModel>>
}