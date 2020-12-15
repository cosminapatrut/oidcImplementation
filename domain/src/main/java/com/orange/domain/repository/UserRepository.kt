package com.orange.domain.repository

import com.orange.domain.model.DomainModel
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserResponseModel
import io.reactivex.Completable
import io.reactivex.Single
import java.time.format.SignStyle

interface UserRepository {
//    fun getUserInfo(bearer: String): Single<UserResponseModel>
    //Mock
    fun getUserInfo(): Single<UserResponseModel>
    fun updateUserInfo(user: UpdateUserModel): Completable
    fun deleteUser(): Completable
}