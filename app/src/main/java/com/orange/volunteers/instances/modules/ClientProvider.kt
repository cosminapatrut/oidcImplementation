package com.orange.volunteers.instances.modules

import android.app.Application
import android.content.Context
import com.orange.volunteers.auth.AuthorizationInterceptor
import com.orange.volunteers.auth.TokenRefreshAuthenticator
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import okhttp3.OkHttpClient
import okhttp3.internal.applyConnectionSpec
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ClientProvider {
    lateinit var context : Context
    var client: OkHttpClient
    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

         client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun getOAuthOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(15L, TimeUnit.SECONDS)
            .connectTimeout(15L, TimeUnit.SECONDS)
            .addInterceptor(AuthorizationInterceptor(OrangeTokenManager.getInstance(context)))
            .authenticator(TokenRefreshAuthenticator(OrangeTokenManager.getInstance(context)))
        return builder.build()
    }
}