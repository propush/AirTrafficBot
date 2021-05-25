package com.skolopendra.radarbot.tlg.service

interface NotificationBotService {

    fun notify(message: String)

    fun watchTrafficChanges()

}
