package me.alekseinovikov.bashim.persistence

import me.alekseinovikov.bashim.model.Quote

interface QuoteRepository {
    suspend fun saveOrUpdate(quote: Quote): Long
    suspend fun getUnreadQuote(userId: Long): Quote?
    suspend fun markQuoteAsRead(quote: Quote, userId: Long)
    suspend fun resetUserReadQuotes(userId: Long)
}