package com.skolopendra.radarbot

import com.skolopendra.radarbot.traffic.service.TrafficService
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZonedDateTime

@Configuration
class TestsConfiguration {

    @Bean
    fun trafficService(): TrafficService = mock {
        on { poll() } doReturn Traffic(ZonedDateTime.now(), emptySet())
    }

}
