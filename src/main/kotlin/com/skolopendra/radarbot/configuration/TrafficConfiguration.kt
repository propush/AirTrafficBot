package com.skolopendra.radarbot.configuration

import com.skolopendra.radarbot.traffic.vo.BoundingBox
import com.skolopendra.radarbot.traffic.vo.Coordinates
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("traffic")
class TrafficConfiguration {

    companion object {
        private const val DEFAULT_ALTITUDE_THRESHOLD_M = 2000L
    }

    lateinit var boundingBox: BoundingBox
    lateinit var centralPoint: Coordinates
    var altitudeThreshold: Long = DEFAULT_ALTITUDE_THRESHOLD_M

}
