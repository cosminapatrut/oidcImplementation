package com.orange.volunteers.auth

data class LoginRequestDTO(
    val token: String?,
    val uuid: String
)