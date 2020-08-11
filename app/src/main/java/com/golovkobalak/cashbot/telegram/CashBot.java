package com.golovkobalak.cashbot.telegram;

import android.content.Context;
import com.golovkobalak.cashbot.R;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;

public class CashBot {
    private final TelegramBot bot;

    public CashBot(final Context context) {
        String token = context.getString(R.string.bot_token);
        bot = new TelegramBot(token);
    }

    public void sendMessage(String chatID, String s) {
        bot.execute(new GetChat(chatID));
        bot.execute(new SendMessage(chatID, s));
    }

    public void setUpdatesListener(UpdatesListener listener) {
        listener.setBot(this);
        bot.setUpdatesListener(listener);
    }
}
