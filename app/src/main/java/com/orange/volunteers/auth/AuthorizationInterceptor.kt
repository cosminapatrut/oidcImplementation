package com.orange.volunteers.auth

import com.orange.volunteers.orangeAuth.OrangeTokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(private val tokenManager: OrangeTokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }

    private fun Request.signedRequest(): Request {
        val accessToken = tokenManager.authState.accessToken ?: ""
        return if (accessToken.isNotBlank())
            newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        else
            newBuilder()
                .removeHeader("Authorization")
                .build()
    }
}