package me.alekseinovikov.bashim.bashimbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BashimBotApplication

fun main(args: Array<String>) {
    runApplication<BashimBotApplication>(*args)
}
