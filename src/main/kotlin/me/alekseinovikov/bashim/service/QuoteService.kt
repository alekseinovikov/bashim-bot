package me.alekseinovikov.bashim.service

import me.alekseinovikov.bashim.model.Quote

interface QuoteService {
    suspend fun getNextUnreadQuoteAndMarkAsRead(userId: Long): Quote?
    suspend fun resetProgress(userId: Long)
}