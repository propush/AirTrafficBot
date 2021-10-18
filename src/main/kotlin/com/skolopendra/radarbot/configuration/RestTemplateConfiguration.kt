package com.skolopendra.radarbot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
@ConfigurationProperties("connection")
class RestTemplateConfiguration {

    companion object {
        private const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 30L
        private const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
    }

    var connectTimeoutSeconds: Long = DEFAULT_CONNECT_TIMEOUT_SECONDS
    var readTimeoutSeconds: Long = DEFAULT_READ_TIMEOUT_SECONDS

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
            .setReadTimeout(Duration.ofSeconds(readTimeoutSeconds))
            .build()

}
