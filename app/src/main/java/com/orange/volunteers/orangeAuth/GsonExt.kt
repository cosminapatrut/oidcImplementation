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

package com.orange.volunteers.orangeAuth

import com.google.gson.Gson
import com.google.gson.GsonBuilder

inline fun <reified T> Any.toJson(): String = Gson().toJson(this, T::class.java)
fun Any.toPrettyJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this, this::class.java)
inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T> Any.hardCopy(): T {
    return this.toJson<T>().fromJson<T>()
}
