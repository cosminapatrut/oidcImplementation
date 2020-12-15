package com.orange.domain.usecases

import android.util.Log
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.repository.UserRepository
import io.reactivex.Completable

class DeleteUserInfoUseCaseImpl(
    private val userRepository: UserRepository
) : DeleteUserInfoUseCase {
    override fun deleteUser(): Completable {
        return userRepository.deleteUser()
            .doOnComplete {
                Log.v("User Delete","success")
            }
            .doOnError {
                Log.e("User Delete error:","${it.message}")
            }
    }
}

interface DeleteUserInfoUseCase {
    fun deleteUser(): Completable
}