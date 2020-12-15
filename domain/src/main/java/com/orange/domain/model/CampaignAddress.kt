package com.orange.domain.model

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CampaignAddress(
    val location: CampaignCoordinates?,
    val cityName: String?,
    val countyName: String?,
    val street: String,
    val postalCode: String,
    val details: String
) : Parcelable
