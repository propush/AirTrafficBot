package com.skolopendra.radarbot.persist.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.skolopendra.radarbot.configuration.BotPersistenceConfiguration
import com.skolopendra.radarbot.persist.entity.BotState
import com.skolopendra.radarbot.persist.service.BotStateService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct

@Service
class FileSystemBotStateServiceImpl(
    private val botPersistenceConfiguration: BotPersistenceConfiguration,
    private val objectMapper: ObjectMapper
) : BotStateService {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val file = File(botPersistenceConfiguration.fileName)

    private lateinit var botState: BotState
    private val botStateLock = Any()

    @PostConstruct
    fun init() {
        try {
            load()
        } catch (e: Exception) {
            botState = BotState(emptySet())
            save()
            log.debug("State initialized, root cause: $e")
        }
    }

    override fun save() {
        synchronized(botStateLock) {
            internalSave()
        }
    }

    override fun load() {
        log.debug("Loading state")
        synchronized(botStateLock) {
            botState = objectMapper.readValue(file, BotState::class.java)
            log.debug("State loaded: $botState")
        }
    }

    override fun saveChatIds(chatIds: Set<Long>) {
        synchronized(botStateLock) {
            botState = botState.copy(chatIds = chatIds)
            internalSave()
        }
    }

    override fun getState(): BotState = synchronized(botStateLock) { botState }

    private fun internalSave() {
        log.debug("Saving state")
        file.writeText(objectMapper.writeValueAsString(botState))
    }

}
