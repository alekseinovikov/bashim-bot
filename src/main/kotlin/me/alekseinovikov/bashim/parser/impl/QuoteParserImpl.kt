package me.alekseinovikov.bashim.parser.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.alekseinovikov.bashim.model.Quote
import me.alekseinovikov.bashim.parser.QuoteParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class QuoteParserImpl : QuoteParser {

    private companion object {
        const val MAIN_PAGE = "https://bash.im"
        const val PAGE_NAVIGATOR_URI = "/index/"
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm")!!
    }


    override fun parse(startPageIndex: Int): Flow<Quote> = flow {
        var currentPageIndex = startPageIndex
        var currentPage = getPageByIndex(currentPageIndex)
        while (currentPage.getCurrentPageIndex() == currentPageIndex++) {
            currentPage.findAllQuotes().forEach { emit(it) }
            currentPage = getPageByIndex(currentPageIndex)
        }

    }

    private fun getPageByIndex(index: Int) = Jsoup.connect(MAIN_PAGE + PAGE_NAVIGATOR_URI + index.toString()).get()!!
    private fun Document.getCurrentPageIndex() =
        this.select("div.pager input.pager__input").firstOrNull()?.attr("value")?.toInt()
            ?: 0

    private fun Document.findAllQuotes(): List<Quote> =
        findAllQuoteFrames()
            .map { quoteFrame ->
                quoteFrame.extractPureQuotes()
            }

    private fun Document.findAllQuoteFrames() = this.select("section.quotes div.quote__frame")!!
    private fun Document.findNextLinks(): Elements? = this.select("a.pager__item")
    private fun Element.findQuoteBody() = this.select("div.quote__body")!!
    private fun Element.findQuoteDate() = this.select("div.quote__header_date")!!
    private fun Element.findVotesText() = this.select("div.quote__total")!!

    private fun Element.extractPureQuotes(): Quote {
        val quoteText = this.findQuoteBody().textNodes().let { parseInternalText(it) }
        val dateTimeString = this.findQuoteDate().textNodes().let { parseInternalText(it) }
        val votesString = this.findVotesText().textNodes().let { parseInternalText(it) }

        val votes = if (votesString.all { it.isDigit() }) votesString.toInt() else 0
        return Quote(
            text = quoteText,
            quoteDateTime = dateTimeString.parseDateTime(),
            votes = votes,
        )
    }

    private fun parseInternalText(textNodes: List<TextNode>): String = textNodes
        .mapNotNull { it.text() }
        .map { it.replace("\n", "") }
        .filter { it.isNotBlank() }
        .joinToString(separator = "\n")
        .trim()

    fun String.parseDateTime(): LocalDateTime = this.replace(" в ", " ")
        .let { LocalDateTime.parse(it, dateTimeFormatter) }

}