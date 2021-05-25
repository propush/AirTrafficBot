package com.skolopendra.radarbot.traffic.service

import com.skolopendra.radarbot.traffic.exception.TrafficException
import com.skolopendra.radarbot.traffic.vo.Traffic
import kotlin.jvm.Throws

interface TrafficService {

    @Throws(TrafficException::class)
    fun poll(): Traffic

}
