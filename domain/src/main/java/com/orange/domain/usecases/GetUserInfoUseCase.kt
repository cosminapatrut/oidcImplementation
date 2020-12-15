package com.orange.domain.usecases

import com.orange.domain.model.DomainModel
import com.orange.domain.model.UserResponseModel
import com.orange.domain.repository.DomainsRepository
import com.orange.domain.repository.UserRepository
import io.reactivex.Single

class GetUserInfoUseCaseImpl(
    private val userRepository: UserRepository
) : GetUserInfoUseCase {
    override fun getUserInfo(): Single<UserResponseModel> {
        return userRepository.getUserInfo()
            .map{ it }
    }
}

interface GetUserInfoUseCase {
    fun getUserInfo(): Single<UserResponseModel>

}