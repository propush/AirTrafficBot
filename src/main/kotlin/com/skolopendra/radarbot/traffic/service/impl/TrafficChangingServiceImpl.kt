package com.skolopendra.radarbot.traffic.service.impl

import com.skolopendra.radarbot.traffic.service.TrafficChangingService
import com.skolopendra.radarbot.traffic.service.TrafficService
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class TrafficChangingServiceImpl(
    private val trafficService: TrafficService
) : TrafficChangingService {

    private var previousTraffic = Traffic(ZonedDateTime.now(), emptySet())
    private val previousTrafficLock = Any()

    override fun getTrafficChanges(): Traffic =
        synchronized(previousTrafficLock) {
            val newTraffic = trafficService.poll()
            val oldTraffic = previousTraffic.copy()
            previousTraffic = newTraffic
            newTraffic - oldTraffic
        }

}
