package com.skolopendra.radarbot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("persistence")
class BotPersistenceConfiguration {

    lateinit var fileName: String

}
