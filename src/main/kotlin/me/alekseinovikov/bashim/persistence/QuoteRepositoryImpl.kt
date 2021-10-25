package me.alekseinovikov.bashim.persistence

import me.alekseinovikov.bashim.model.Quote
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class QuoteRepositoryImpl(private val databaseClient: DatabaseClient) : QuoteRepository {

    override suspend fun saveOrUpdate(quote: Quote): Long = databaseClient.sql(
        """
        INSERT INTO quotes(hash, text, posted_date_time, votes_count)
        VALUES(:hash, :text, :posted, :votes)
        ON CONFLICT (hash) DO UPDATE 
            SET text = excluded.text,
                posted_date_time = excluded.posted_date_time,
                votes_count = excluded.votes_count
        RETURNING id
    """.trimIndent()
    )
        .bind("hash", quote.md5Hash)
        .bind("text", quote.text)
        .bind("posted", quote.quoteDateTime)
        .bind("votes", quote.votes)
        .fetch()
        .awaitSingle()
        .values
        .first() as Long

    override suspend fun markQuoteAsRead(quote: Quote, userId: Long): Unit {
        val sql = databaseClient.sql(
            """
        INSERT INTO quotes_read(user_id, quote_hash) 
        VALUES(:userId, :hash) ON CONFLICT(user_id, quote_hash) DO NOTHING
    """.trimIndent()
        ).bind("userId", userId)
            .bind("hash", quote.md5Hash)
            .fetch()
            .awaitSingleOrNull()
    }

    override suspend fun resetUserReadQuotes(userId: Long) {
        databaseClient.sql(
            """
        DELETE FROM quotes_read WHERE user_id = :userId
    """.trimIndent()
        )
            .bind("userId", userId)
            .fetch()
            .awaitRowsUpdated()
    }

    override suspend fun getUnreadQuote(userId: Long): Quote? = databaseClient.sql(
        """
        SELECT q.* FROM quotes as q 
        LEFT JOIN quotes_read as qr ON qr.user_id = :userId AND qr.quote_hash = q.hash 
        WHERE qr.quote_hash IS NULL 
        ORDER BY q.votes_count DESC 
        LIMIT 1
    """.trimIndent()
    )
        .bind("userId", userId)
        .fetch()
        .awaitSingleOrNull()
        ?.parseQuote()

    private fun Map<String, Any>.parseQuote() = Quote(
        text = this["text"] as String,
        quoteDateTime = this["posted_date_time"] as LocalDateTime,
        votes = this["votes_count"] as Int
    )

}