package com.orange.domain.usecases

import android.util.Log
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserResponseModel
import com.orange.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single

class PutUserInfoUseCaseImpl(
    private val userRepository: UserRepository
) : PutUserInfoUseCase {
    override fun updateUserInfo(user: UpdateUserModel): Completable {
        return userRepository.updateUserInfo(user)
            .doOnComplete {
                Log.v("User Update","success")
            }
            .doOnError {
                Log.e("User Update error:","${it.message}")
            }
    }
}

interface PutUserInfoUseCase {
    fun updateUserInfo(user: UpdateUserModel): Completable

}