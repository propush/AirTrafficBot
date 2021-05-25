package com.skolopendra.radarbot.traffic.vo

import java.time.ZonedDateTime

data class Traffic(
    val trafficDt: ZonedDateTime,
    val crafts: Set<Craft>
) {

    operator fun minus(traffic: Traffic): Traffic =
        Traffic(
            this.trafficDt,
            craftsDifference(traffic.crafts, this.crafts)
        )

    private fun craftsDifference(crafts1: Set<Craft>, crafts2: Set<Craft>): Set<Craft> =
        crafts2
            .filterNot { crafts1.map(Craft::callSign).contains(it.callSign) }
            .toSet()

}
