package me.alekseinovikov.bashim.service.impl

import me.alekseinovikov.bashim.model.Quote
import me.alekseinovikov.bashim.persistence.QuoteRepository
import me.alekseinovikov.bashim.service.QuoteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuoteServiceImpl(private val quoteRepository: QuoteRepository) : QuoteService {

    @Transactional
    override suspend fun getNextUnreadQuoteAndMarkAsRead(userId: Long): Quote? {
        val unread = quoteRepository.getUnreadQuote(userId)
        unread?.run {
            quoteRepository.markQuoteAsRead(this, userId)
        }

        return unread
    }

    @Transactional
    override suspend fun resetProgress(userId: Long) =
        quoteRepository.resetUserReadQuotes(userId)

}