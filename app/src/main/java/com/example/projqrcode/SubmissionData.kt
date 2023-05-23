package com.example.projqrcode

import com.example.projqrcode.MainActivity

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

data class SubmissionData(
    val qrCodeMessage: String,
    val ra: String,
    val location: LatLng?,
    val qrCodeBitmap: Bitmap?,
    val photoBitmap: Bitmap?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(LatLng::class.java.classLoader),
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(qrCodeMessage)
        parcel.writeString(ra)
        parcel.writeParcelable(location, flags)
        parcel.writeParcelable(qrCodeBitmap, flags)
        parcel.writeParcelable(photoBitmap, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubmissionData> {
        override fun createFromParcel(parcel: Parcel): SubmissionData {
            return SubmissionData(parcel)
        }

        override fun newArray(size: Int): Array<SubmissionData?> {
            return arrayOfNulls(size)
        }
    }
}

