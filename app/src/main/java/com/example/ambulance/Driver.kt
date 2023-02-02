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
        vehiclenumber: String?
    )
}