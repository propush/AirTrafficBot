package com.skolopendra.radarbot.traffic.service.impl

import com.skolopendra.radarbot.configuration.TrafficConfiguration
import com.skolopendra.radarbot.traffic.exception.TrafficException
import com.skolopendra.radarbot.traffic.service.TrafficService
import com.skolopendra.radarbot.traffic.vo.Craft
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.opensky.api.OpenSkyApi
import org.opensky.model.StateVector
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Service
@Profile("!test")
class OpenSkyTrafficServiceImpl(
    private val trafficConfiguration: TrafficConfiguration
) : TrafficService {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val openSkyApi = OpenSkyApi()

    @Throws(TrafficException::class)
    override fun poll(): Traffic {
        log.debug("Fetching traffic")
        try {
            val states = openSkyApi.getStates(
                0,
                null,
                OpenSkyApi.BoundingBox(
                    trafficConfiguration.boundingBox.minLat,
                    trafficConfiguration.boundingBox.maxLat,
                    trafficConfiguration.boundingBox.minLon,
                    trafficConfiguration.boundingBox.maxLon,
                )
            )
            if (states?.states == null) {
                log.error("Null states returned")
                return Traffic(
                    ZonedDateTime.now(),
                    emptySet()
                )
            }
            return Traffic(
                toZonedDateTime(states.time),
                states.states.mapNotNull(::stateVectorToCraft).filter(::craftFilter).toSet()
            ).also {
                log.debug("Traffic fetched: $it")
            }
        } catch (e: Exception) {
            throw TrafficException("Error fetching traffic from OpenSky: $e", e)
        }
    }

    private fun craftFilter(craft: Craft): Boolean =
        craft.altitude <= trafficConfiguration.altitudeThreshold

    private fun toZonedDateTime(time: Int?): ZonedDateTime =
        if (time != null) {
            ZonedDateTime.from(Instant.ofEpochSecond(time.toLong()).atZone(ZoneId.systemDefault()))
        } else {
            ZonedDateTime.now()
        }

    private fun stateVectorToCraft(stateVector: StateVector): Craft =
        Craft(
            stateVector.callsign.trim(),
            stateVector.baroAltitude,
            stateVector.latitude,
            stateVector.longitude,
            stateVector.velocity,
            stateVector.heading,
            countDistance(
                stateVector.latitude,
                stateVector.longitude,
                trafficConfiguration.centralPoint.lat,
                trafficConfiguration.centralPoint.lon
            )
        )

    private fun countDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6372.8 //Earth's Radius In kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1r = Math.toRadians(lat1)
        val lat2r = Math.toRadians(lat2)
        val a = sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(lat1r) * cos(lat2r)
        val c = 2 * asin(sqrt(a))
        return BigDecimal(earthRadius * c).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

}
