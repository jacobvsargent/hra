package com.example.houserules

//house model class
class House(var id: String, var name: String, var GPS_lat: String, var GPS_long: String, var qr_key: String) {
    constructor(): this("", "", "", "", "")
}