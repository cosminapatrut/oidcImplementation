package com.orange.domain.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class CampaignPicturesModel(
val name: String?,
val uri: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CampaignPicturesModel> {
        override fun createFromParcel(parcel: Parcel): CampaignPicturesModel {
            return CampaignPicturesModel(parcel)
        }

        override fun newArray(size: Int): Array<CampaignPicturesModel?> {
            return arrayOfNulls(size)
        }
    }
}