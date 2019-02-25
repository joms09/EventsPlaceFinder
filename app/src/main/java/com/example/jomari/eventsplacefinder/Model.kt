package com.example.jomari.eventsplacefinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Model(
    var Id: String? = null,
    var Image: String? = null,
    var eventname: String? = null,
    var Status: String? = null,
    var eventtype: String? = null,
    var Count: Int = 0,
    var Address: String? = null,
    var Phone: String? = null,
    var Cpnumber: String? = null,
    var EventDescription: String? = null,
    var Amenities: String? = null,
    var MaxPeople: String? = null,
    var MinPeople : String? = null,
    var MinPrice : String? = null,
    var bHours : String? =  null,
    var eventStatus : String? = null


) : Parcelable {


    constructor() : this("", "", "", "", "", 0, "", "",
        "", "", "", "", "", "", "", "")
}
