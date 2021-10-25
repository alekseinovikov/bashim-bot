package me.alekseinovikov.bashim

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class BashimBotApplication

fun main(args: Array<String>) {
    runApplication<BashimBotApplication>(*args)
}
