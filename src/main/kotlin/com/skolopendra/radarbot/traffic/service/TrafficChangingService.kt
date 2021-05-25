package com.skolopendra.radarbot.traffic.service

import com.skolopendra.radarbot.traffic.vo.Traffic

interface TrafficChangingService {

    fun getTrafficChanges(): Traffic

}
