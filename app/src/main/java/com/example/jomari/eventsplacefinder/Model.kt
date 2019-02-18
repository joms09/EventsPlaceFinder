package com.example.jomari.eventsplacefinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Model(var Id : String? = null,
            var Image: String? = null,
            var Name : String? = null,
            var Status : String? = null,
            var Type : String? = null,
            var Count: Int = 0,
            var Address: String? = null
) : Parcelable {


    constructor(): this("","","", "", "", 0, "")
}
