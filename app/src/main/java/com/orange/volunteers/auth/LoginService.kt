package com.orange.volunteers.auth

import io.reactivex.Single

interface LoginService {
    val cviToken: String
    fun login(userLoginRequestDTO: LoginRequestDTO): Single<LoginResponseDTO>
    fun clear()
}