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

package com.orange.volunteers.util

open class SingletonHolder <out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    open fun getInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}
