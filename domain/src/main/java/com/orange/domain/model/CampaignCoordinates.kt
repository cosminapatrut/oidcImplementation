package com.orange.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CampaignCoordinates(
    val latitude: Double?,
    val longitude: Double?
) : Parcelable