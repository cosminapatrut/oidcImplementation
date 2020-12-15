/*
 * Software Name : Android Smart Voice Kit - smartvoicekitcommons
 * SPDX-FileCopyrightText: Copyright (c) 2017-2020 Orange
 *
 * This software is confidential and proprietary information of Orange.
 * You are not allowed to disclose such Confidential Information nor to copy, use, modify, or distribute it in whole or in part without the prior written consent of Orange.
 *
 * Author: The current developers of this code can be found in the Authors.txt file at the root of the project.
 *  Software description: Smart Voice Kit is the Android SDK that allows to integrate the Smart Voice Hub voice assistant into your app.
 *  Module description: A set of common utilities used across all SmartVoiceKit modules.
 */

package com.orange.volunteers.orangeAuth

import com.orange.domain.model.UserResponseModel
import io.reactivex.Single

/**
 * An interface for the injection of the access token provided by an external
 * authentication service (ex: OIDC) and user info.
 *
 */
interface ExternalAuthService {
    /**
     * Returns asynchronously the access  token
     */
    fun getTokenAsync(): Single<ExternalIdToken> = Single.error(Throwable("missing implementation"))

    /**
     * Returns synchronously the access token
     */
    fun getTokenSync(): ExternalIdToken

    /**
     * Returns asynchronously the user info
     *
     */
    fun getUserInfoAsync(): Single<UserResponseModel>

    /**
     * Returns synchronously th user info
     *
     */
    fun getUserInfoSync(): UserResponseModel

    /**
     * This method can be used, if needed,  to clear the access token injected with
     * getTokenAsync() adn getTokenSync()
     * Leave empty if token removal is not necessary or if it's done in another part of your app
     *
     */
    fun clearToken()

    /**
     * This method can be used, if needed,  to clear the user info injected with
     * getUserInfoAsync() adn getUserInfoSync()
     * Leave empty if user info removal is not necessary or if it's done in another part of your app
     *
     */
    fun clearUserInfo()

}

/**
 * Represents the identifier of the user provided by an external auth service
 *
 * @property id User id provided by the external authentication service. Can be left empty.
 * @property externalToken The  access token provided by the external authentication service
 */
data class ExternalIdToken(
    @Deprecated("Used for legacy anonymous login mechanism. Not used anymore on most of the tenants") val id: String,
    val externalToken: String? = null
)
