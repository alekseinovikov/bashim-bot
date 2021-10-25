package me.alekseinovikov.bashim.jobs

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import me.alekseinovikov.bashim.parser.QuoteParser
import me.alekseinovikov.bashim.persistence.QuoteRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ParsingJob(
    private val quoteParser: QuoteParser,
    private val quoteRepository: QuoteRepository
) {

    private val startIndex = 1

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24L) //Parse every 24 hours
    fun parse() = runBlocking {
        quoteParser.parse(startIndex)
            .collect { quote ->
                quoteRepository.saveOrUpdate(quote)
            }
    }

}