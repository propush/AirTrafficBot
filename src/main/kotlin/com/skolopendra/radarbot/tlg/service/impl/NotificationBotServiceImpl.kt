package com.skolopendra.radarbot.tlg.service.impl

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.skolopendra.radarbot.configuration.NotificationBotConfiguration
import com.skolopendra.radarbot.persist.service.BotStateService
import com.skolopendra.radarbot.tlg.service.NotificationBotService
import com.skolopendra.radarbot.traffic.exception.TrafficException
import com.skolopendra.radarbot.traffic.service.TrafficChangingService
import com.skolopendra.radarbot.traffic.vo.Craft
import com.skolopendra.radarbot.traffic.vo.Traffic
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import javax.annotation.PostConstruct
import kotlin.concurrent.thread

@Service
class NotificationBotServiceImpl(
    private val notificationBotConfiguration: NotificationBotConfiguration,
    private val botStateService: BotStateService,
    private val trafficChangingService: TrafficChangingService
) : NotificationBotService {

    companion object {
        private const val DEFAULT_KEEP_ALIVE_PERIOD_MS = 1 * 60 * 60 * 1000L
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    private val chatIds = mutableSetOf<Long>()

    private val tgBot =
        bot {
            token = notificationBotConfiguration.apiKey
            dispatch {
                command("start") {
                    log.info("Bot started at ${message.chat}")
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                    chatIds.add(message.chat.id)
                    saveState()
                }
                command("stop") {
                    log.info("Bot stopped in ${message.chat}")
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Bye!")
                    chatIds.remove(message.chat.id)
                    saveState()
                }
            }
        }.also {
            it.startPolling()
        }

    private fun saveState() {
        botStateService.saveChatIds(chatIds)
    }

    @PostConstruct
    fun init() {
        log.info("Bot started")
        chatIds.addAll(botStateService.getState().chatIds)
        log.info("Restored chat(s): ${chatIds.size}")
        notify("Bot restarted")
        thread {
            while (true) {
                sleep(DEFAULT_KEEP_ALIVE_PERIOD_MS)
                notify("Traffic bot is alive and running")
            }
        }
        thread {
            while (true) {
                sleep(notificationBotConfiguration.trafficPollingIntervalMs)
                watchTrafficChanges()
            }
        }
    }

    override fun notify(message: String) {
        log.debug("Sending message to ${chatIds.size} chats: $message")
        chatIds.forEach {
            tgBot.sendMessage(chatId = ChatId.fromId(it), text = message)
        }
    }

    override fun watchTrafficChanges() {
        try {
            val newTraffic = trafficChangingService.getTrafficChanges()
            if (newTraffic.crafts.isEmpty()) {
                log.debug("No new traffic detected")
                return
            }
            log.debug("New traffic detected: $newTraffic")
            notify("New traffic detected:\n${printTraffic(newTraffic)}")
        } catch (e: TrafficException) {
            log.error("Error watching traffic", e)
        }
    }

    private fun printTraffic(traffic: Traffic): String =
        traffic.crafts.joinToString(separator = "\n", transform = ::printCraft)

    private fun printCraft(craft: Craft): String =
        with(craft) { "$callSign, alt=$altitude m, distance=$distance km, spd=$speed km/h, coords=$lat,$lon, hdg=$heading" }

}
