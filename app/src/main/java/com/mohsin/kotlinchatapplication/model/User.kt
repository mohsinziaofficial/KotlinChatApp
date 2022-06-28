package com.mohsin.kotlinchatapplication.model

class User {
    var uid : String? = null
    var name : String? = null
    var phoneNumber : String? = null
    var profileImg : String? = null

    constructor() {}

    constructor(
        uid : String?,
        name : String?,
        phoneNumber : String?,
        profileImg : String?,
    ) {
        this.uid = uid
        this.name = name
        this.phoneNumber = phoneNumber
        this.profileImg = profileImg
    }
}