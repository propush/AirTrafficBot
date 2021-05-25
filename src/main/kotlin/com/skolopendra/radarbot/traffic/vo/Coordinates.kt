package com.skolopendra.radarbot.traffic.vo

import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
data class Coordinates(
    val lat: Double,
    val lon: Double
)
