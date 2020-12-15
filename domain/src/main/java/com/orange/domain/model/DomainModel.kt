package com.orange.domain.model

import android.os.Parcel
import android.os.Parcelable

data class DomainModel(
    val name: String?,
    val uuid: String? = null,
    @Transient var isInterested: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(name)
        parcel.writeByte(if (isInterested) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DomainModel> {
        override fun createFromParcel(parcel: Parcel): DomainModel {
            return DomainModel(parcel)
        }

        override fun newArray(size: Int): Array<DomainModel?> {
            return arrayOfNulls(size)
        }
    }
}