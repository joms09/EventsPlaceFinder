package com.example.jomari.eventsplacefinder

class ReviewsRatings (val id: String?, val account: String, val email: String, val reviews: String, val rating: Int){
    constructor() : this("","", "", "",0){

    }
}