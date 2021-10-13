package me.alekseinovikov.bashim.parser

import kotlinx.coroutines.flow.Flow
import me.alekseinovikov.bashim.model.Quote

interface QuoteParser {

    fun parse(startPageIndex: Int): Flow<Quote>

}