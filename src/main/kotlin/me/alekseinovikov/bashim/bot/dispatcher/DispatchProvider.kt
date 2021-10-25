package me.alekseinovikov.bashim.bot.dispatcher

import com.github.kotlintelegrambot.dispatcher.Dispatcher

interface DispatchProvider {
    fun provide(): Dispatcher.() -> Unit
}