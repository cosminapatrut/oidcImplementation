package com.orange.volunteers.campaigns.details

import com.orange.data.service.enrollment.EnrollmentService
import com.orange.volunteers.auth.AuthorizationInterceptor
import com.orange.volunteers.auth.TokenRefreshAuthenticator
import com.orange.volunteers.instances.modules.ClientProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class BaseClient {

    companion object {

        fun apiFactory(): Retrofit {
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)

            return  Retrofit.Builder()
                .baseUrl("https://www-dev.orange.ro/csr-backend/")
                .client(ClientProvider.getOAuthOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
    }
}