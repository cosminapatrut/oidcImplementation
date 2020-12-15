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

package com.orange.data.service.user

import com.google.gson.annotations.SerializedName
import com.orange.domain.model.UserContact
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UserLocation

data class UserResponseDTO(
    @SerializedName("contact")
    val contact: UserContact?,
    @SerializedName("notificationsEnabled")
    val notificationsEnabled: Boolean = false,
    @SerializedName("locationSharingEnabled")
    var locationSharingEnabled: Boolean = false,
    @SerializedName("location")
    var location: UserLocation?,
    @SerializedName("role")
    val role: String?,//"ADMIN"
    @SerializedName("preferredDomains")
    val preferredDomains: ArrayList<DomainModel>
)
