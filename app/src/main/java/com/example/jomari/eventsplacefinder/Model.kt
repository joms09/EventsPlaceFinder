package com.example.jomari.eventsplacefinder

class Model {
    var Id : String? = null
    var Image: String? = null
    var Name : String? = null
    var Status : String? = null
    var Type : String? = null
    var Count: Int = 0

    constructor():this("","","", "", "", 0) {

    }


    constructor(Id: String?, Image: String?, Name: String?, Status: String?, Type : String?, Count: Int) {
        this.Id = Id
        this.Image = Image
        this.Name = Name
        this.Status = Status
        this.Type = Type
        this.Count = Count
    }
}