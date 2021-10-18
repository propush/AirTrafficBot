package com.skolopendra.radarbot

import com.skolopendra.radarbot.configuration.BotPersistenceConfiguration
import com.skolopendra.radarbot.configuration.NotificationBotConfiguration
import com.skolopendra.radarbot.configuration.RestTemplateConfiguration
import com.skolopendra.radarbot.configuration.TrafficConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    NotificationBotConfiguration::class,
    BotPersistenceConfiguration::class,
    TrafficConfiguration::class,
    RestTemplateConfiguration::class
)
class RadarbotApplication

fun main(args: Array<String>) {
    runApplication<RadarbotApplication>(*args)
}
