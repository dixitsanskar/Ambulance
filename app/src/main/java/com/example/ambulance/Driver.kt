package com.example.ambulance

import android.provider.ContactsContract.CommonDataKinds.Email

class Driver {
    internal constructor() {}

    var name: String? = null
    var email: String? = null
    var password: String? = null
    var vehiclenumber: String? = null
    var lat: String? = null
    var lng: String? = null

    constructor(
        name: String?, email: String?, password: String?,
        vehiclenumber: String?,
        lat: String?,
        lng: String?
    ){
        this.email = email
        this.name = name
        this.password = password
        this.vehiclenumber = vehiclenumber
        this.lat = lat
        this.lng = lng

    }
}