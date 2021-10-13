package me.alekseinovikov.bashim.model

import org.apache.commons.codec.digest.DigestUtils
import java.time.LocalDateTime
import java.util.*

data class Quote(
    val text: String,
    val quoteDateTime: LocalDateTime,
    val votes: Int
) {
    val md5Hash: String = DigestUtils.md5Hex(text).uppercase(Locale.getDefault())
}