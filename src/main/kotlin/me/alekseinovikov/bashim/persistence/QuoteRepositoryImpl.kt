package me.alekseinovikov.bashim.persistence

import me.alekseinovikov.bashim.model.Quote
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.stereotype.Repository

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

}