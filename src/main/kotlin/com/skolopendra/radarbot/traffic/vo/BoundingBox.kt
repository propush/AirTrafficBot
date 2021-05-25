package com.skolopendra.radarbot.traffic.vo

import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
data class BoundingBox(
    var minLat: Double = 0.0,
    var maxLat: Double = 0.0,
    var minLon: Double = 0.0,
    var maxLon: Double = 0.0
)
