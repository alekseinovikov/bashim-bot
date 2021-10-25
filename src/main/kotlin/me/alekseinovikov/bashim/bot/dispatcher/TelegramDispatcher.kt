package me.alekseinovikov.bashim.bot.dispatcher

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.runBlocking
import me.alekseinovikov.bashim.model.Quote
import me.alekseinovikov.bashim.service.QuoteService
import org.springframework.stereotype.Component

@Component
class TelegramDispatcher(private val quoteService: QuoteService) : DispatchProvider {

    private val startMessage =
        "Привет. Я буду отправлять тебе топовые цитаты с сайта bash.im по твоему запросу. Вот пример."
    private val nextButton =
        InlineKeyboardButton.CallbackData(text = "Следущая", callbackData = ButtonTypes.NEXT_QUOTE.name)
    private val resetButton =
        InlineKeyboardButton.CallbackData(text = "Начать сначала", callbackData = ButtonTypes.RESET.name)

    override fun provide(): Dispatcher.() -> Unit = {
        command("start") {
            bot.sendMessage(ChatId.fromId(message.chat.id), startMessage)
            processNextMessage(bot, message.from!!.id, message.chat.id)
        }

        callbackQuery(callbackData = ButtonTypes.NEXT_QUOTE.name) {
            processNextMessage(bot, callbackQuery.message!!.from!!.id, callbackQuery.message!!.chat.id)
        }

        callbackQuery(callbackData = ButtonTypes.RESET.name) {
            processReset(bot, callbackQuery.message!!.from!!.id, callbackQuery.message!!.chat.id)
        }
    }

    private fun processNextMessage(bot: Bot, userId: Long, chatId: Long) = runBlocking {
        quoteService.getNextUnreadQuoteAndMarkAsRead(userId)
            ?.let { sendQuote(bot, it, chatId) }
            ?: sendResetButtonOnly(bot, chatId)
    }

    private fun processReset(bot: Bot, userId: Long, chatId: Long) = runBlocking {
        quoteService.resetProgress(userId)
        processNextMessage(bot, userId, chatId)
    }

    private fun sendQuote(bot: Bot, quote: Quote, chatId: Long) {
        bot.sendMessage(
            chatId = ChatId.fromId(chatId),
            text = quote.text,
            replyMarkup = InlineKeyboardMarkup.create(
                listOf(
                    nextButton,
                    resetButton
                )
            )
        )
    }

    private fun sendResetButtonOnly(bot: Bot, chatId: Long) {
        bot.sendMessage(
            chatId = ChatId.fromId(chatId),
            text = "Вы прочли все доступные цитаты. Нажмите на кнопку ниже, чтобы начать сначала!",
            replyMarkup = InlineKeyboardMarkup.create(listOf(resetButton))
        )
    }

}