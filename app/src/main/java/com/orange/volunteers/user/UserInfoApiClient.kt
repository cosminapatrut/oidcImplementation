/*
 * Software Name : Android Smart Voice Kit - app
 * SPDX-FileCopyrightText: Copyright (c) 2017-2020 Orange
 *
 * This software is confidential and proprietary information of Orange.
 * You are not allowed to disclose such Confidential Information nor to copy, use, modify, or distribute it in whole or in part without the prior written consent of Orange.
 *
 * Author: The current developers of this code can be found in the Authors.txt file at the root of the project.
 *  Software description: Smart Voice Kit is the Android SDK that allows to integrate the Smart Voice Hub voice assistant into your app.
 *  Module description: Demo application for SmartVoiceKit SDK integration
 */

package com.orange.volunteers.user

import android.util.Log
import android.widget.Toast
import com.orange.data.service.user.UserService
import com.orange.data.service.user.UserResponseDTO
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal class UserInfoApiClient(baseUrl: String) {

    private val retrofit: Retrofit
    private val api: UserService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(UserService::class.java)
    }

    fun getUserInfo(): Single<UserResponseDTO> {
        return api.getUserInfo()
            .doOnError {
                Log.e("ERROR", "$it")
            }
    }
}
