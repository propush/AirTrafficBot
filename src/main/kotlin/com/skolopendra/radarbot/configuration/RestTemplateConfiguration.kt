package com.skolopendra.radarbot.configuration

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestTemplateConfiguration {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(30))
            .build()

}
