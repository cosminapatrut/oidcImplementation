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

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * An event bus designed to allowing application to communicate efficiently
 * Uses object so we have a singleton instance
 */
object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}
