package com.skolopendra.radarbot.traffic.vo


import com.fasterxml.jackson.annotation.JsonProperty

data class RawStateVector(
    @JsonProperty("time")
    val time: Int?,
    @JsonProperty("states")
    val states: List<List<Any?>>?
)
