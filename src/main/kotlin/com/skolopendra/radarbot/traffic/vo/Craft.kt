package com.skolopendra.radarbot.traffic.vo

data class Craft(
    val callSign: String,
    val altitude: Double,
    val lat: Double,
    val lon: Double,
    val speed: Double,
    val heading: Double,
    val distance: Double
)
