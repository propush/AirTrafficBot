package com.skolopendra.radarbot.traffic.service.impl

import com.skolopendra.radarbot.traffic.service.TrafficService
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

abstract class BaseTrafficServiceImpl : TrafficService {

    protected fun distance(
        lat1: Double, lon1: Double, el1: Double,
        lat2: Double, lon2: Double, el2: Double
    ): Double {
        val earthRadius = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = earthRadius * c * 1000 // convert to meters
        val height = el1 - el2
        distance = distance.pow(2.0) + height.pow(2.0)
        return BigDecimal(sqrt(distance)).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
