package com.skolopendra.radarbot.traffic.service.impl

import com.skolopendra.radarbot.configuration.TrafficConfiguration
import com.skolopendra.radarbot.traffic.exception.TrafficException
import com.skolopendra.radarbot.traffic.vo.Craft
import com.skolopendra.radarbot.traffic.vo.RawStateVector
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
@Profile("!test")
class OpenSkyTrafficServiceImpl(
    private val trafficConfiguration: TrafficConfiguration,
    private val restTemplate: RestTemplate
) : BaseTrafficServiceImpl() {

    companion object {
        private const val BASE_URL = "https://opensky-network.org/api"
        private const val STATES_RQ = "/states/all"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    @Throws(TrafficException::class)
    override fun poll(): Traffic {
        log.debug("Fetching traffic")
        val url = "$BASE_URL/$STATES_RQ" + getRequestParams()
        val responseEntity = try {
            restTemplate.getForEntity(url, RawStateVector::class.java)
        } catch (e: Exception) {
            throw TrafficException("Error sending http request to url $url", e)
        }
        if (responseEntity.statusCode != HttpStatus.OK) {
            throw TrafficException("Bad http return code: ${responseEntity.statusCodeValue}")
        }
        val rawStateVector = responseEntity.body ?: throw TrafficException("Empty state vector")
        return Traffic(
            toZonedDateTime(rawStateVector.time),
            rawStateVector
                .states
                ?.map(::stateVectorToCraft)
                ?.filterNot(::craftFilter)
                ?.toSet()
                ?: emptySet()
        ).also {
            log.debug("Traffic after deserialization: $it")
        }
    }

    private fun getRequestParams(): String =
        with(trafficConfiguration.boundingBox) { "?lamin=$minLat&lomin=$minLon&lamax=$maxLat&lomax=$maxLon" }

    private fun craftFilter(craft: Craft): Boolean =
        craft.altitude <= trafficConfiguration.altitudeThreshold

    private fun toZonedDateTime(time: Int?): ZonedDateTime =
        if (time != null) {
            ZonedDateTime.from(Instant.ofEpochSecond(time.toLong()).atZone(ZoneId.systemDefault()))
        } else {
            ZonedDateTime.now()
        }

    private fun stateVectorToCraft(params: List<Any?>): Craft {
        val lat = paramToDouble(params[6])
        val lon = paramToDouble(params[5])
        return Craft(
            callSign = params[1]?.toString()?.trim() ?: "N/A",
            altitude = paramToDouble(params[7]),
            lat = lat,
            lon = lon,
            speed = BigDecimal(paramToDouble(params[9]) * 3.6).setScale(2, RoundingMode.HALF_UP).toDouble(),
            heading = paramToDouble(params[10]),
            distance = distance(
                lat, lon, 0.0,
                trafficConfiguration.centralPoint.lat,
                trafficConfiguration.centralPoint.lon,
                0.0
            )
        )
    }

    private fun paramToDouble(param: Any?): Double = (param?.toString() ?: "0.0").toDouble()

}
