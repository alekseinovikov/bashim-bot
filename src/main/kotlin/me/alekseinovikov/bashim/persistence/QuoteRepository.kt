package me.alekseinovikov.bashim.persistence

import me.alekseinovikov.bashim.model.Quote

interface QuoteRepository {
    suspend fun saveOrUpdate(quote: Quote): Long
}