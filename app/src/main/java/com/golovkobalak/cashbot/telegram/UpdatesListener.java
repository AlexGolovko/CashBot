package com.golovkobalak.cashbot.telegram;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.repo.*;
import com.golovkobalak.cashbot.telegram.strategy.AbstractMessageStrategy;
import com.golovkobalak.cashbot.telegram.strategy.CashMessageStrategy;
import com.golovkobalak.cashbot.telegram.strategy.CommandStrategy;
import com.golovkobalak.cashbot.telegram.strategy.Strategy;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdatesListener implements com.pengrad.telegrambot.UpdatesListener {
    private CashBot bot;
    private Strategy strategy;

    public UpdatesListener(final Context context) {
        AbstractMessageStrategy.init(context);
    }

    @Override
    public int process(List<Update> updates) {
        {
            for (final Update update : updates) {
                try {
                    Long chatTelegramId = update.message().chat().id();
                    if (update.message().text().startsWith("/")) {
                        strategy = new CommandStrategy();
                    } else {
                        strategy = new CashMessageStrategy();
                    }
                    final String response = strategy.handle(update);
                    bot.sendMessage(chatTelegramId.toString(), response);
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), this.toString(), e);
                }
            }
            return CONFIRMED_UPDATES_ALL;
        }
    }

    public void setBot(CashBot bot) {
        this.bot = bot;
    }
}
