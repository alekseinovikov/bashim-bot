package me.alekseinovikov.bashim.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import me.alekseinovikov.bashim.bot.dispatcher.DispatchProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfiguration {

    @Bean
    fun botBean(
        props: BotProperties,
        dispatchProvider: DispatchProvider
    ) = bot {
        token = props.token
        dispatch(dispatchProvider.provide())
    }

}