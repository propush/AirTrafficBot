package com.skolopendra.radarbot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notification")
class NotificationBotConfiguration {

    companion object {
        private const val DEFAULT_TRAFFIC_POLLING_INTERVAL_MS = 60 * 1000L
    }

    lateinit var apiKey: String
    var trafficPollingIntervalMs: Long = DEFAULT_TRAFFIC_POLLING_INTERVAL_MS

}
