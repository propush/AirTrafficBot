package com.skolopendra.radarbot.persist.service

import com.skolopendra.radarbot.persist.entity.BotState

interface BotStateService {

    fun getState(): BotState

    fun save()

    fun load()

    fun saveChatIds(chatIds: Set<Long>)

}
