package com.skolopendra.radarbot.traffic.service.impl

import com.skolopendra.radarbot.traffic.service.TrafficService
import com.skolopendra.radarbot.traffic.vo.Craft
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturnConsecutively
import org.mockito.kotlin.mock
import java.time.ZoneId
import java.time.ZonedDateTime

internal class TrafficChangingServiceImplTest {

    private lateinit var trafficChangingServiceImpl: TrafficChangingServiceImpl
    private lateinit var trafficService: TrafficService
    private lateinit var craft1: Craft
    private lateinit var craft2: Craft
    private lateinit var craft3: Craft
    private lateinit var craft4: Craft

    @BeforeEach
    fun setUp() {
        craft1 = Craft("1", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        craft2 = Craft("2", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        craft3 = Craft("3", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        craft4 = Craft("4", 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        trafficService = mock {
            on { poll() } doReturnConsecutively listOf(
                Traffic(
                    ZonedDateTime.of(2021, 1, 25, 10, 11, 12, 0, ZoneId.systemDefault()),
                    setOf(craft1, craft2, craft3)
                ),
                Traffic(
                    ZonedDateTime.of(2021, 1, 25, 10, 12, 13, 0, ZoneId.systemDefault()),
                    setOf(craft2.copy(altitude = 1.0), craft3, craft4)
                )
            )
        }
        trafficChangingServiceImpl = TrafficChangingServiceImpl(trafficService)
        trafficChangingServiceImpl.getTrafficChanges()
    }

    @Test
    fun getTrafficChanges() {
        trafficChangingServiceImpl.getTrafficChanges().let {
            assertEquals(12, it.trafficDt.minute)
            assertEquals(
                listOf("4"),
                it.crafts.map(Craft::callSign).toList()
            )
        }
    }

}
