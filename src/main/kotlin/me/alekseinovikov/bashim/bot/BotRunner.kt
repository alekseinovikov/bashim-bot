package me.alekseinovikov.bashim.bot

import com.github.kotlintelegrambot.Bot
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class BotRunner(private val bot: Bot): CommandLineRunner {
    override fun run(vararg args: String?) = bot.startPolling()
}