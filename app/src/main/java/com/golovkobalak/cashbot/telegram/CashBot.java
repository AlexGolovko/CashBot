package com.golovkobalak.cashbot.telegram;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.R;
import com.golovkobalak.cashbot.repo.*;
import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CashBot {
    private final TelegramBot bot;
    private final CashStateRepo cashStateRepo;
    private final ChatRepo chatRepo;
    private final Gson gson;

    public CashBot(Context context) {
        String token = context.getString(R.string.bot_token);
        bot = new TelegramBot(token);
        chatRepo = new ChatRepo(context);
        cashStateRepo = new CashStateRepo(context);
        gson = new Gson();
    }

    public void sendMessage(String chatID, String s) {
        bot.execute(new GetChat(chatID));
        bot.execute(new SendMessage(chatID, s));
    }

    public void postConstruct() {
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                {
                    for (Update update : updates) {
                        try {
                            Long chatTelegramId = update.message().chat().id();
                            Chat chat = chatRepo.findByChatId(chatTelegramId);
                            if (chat == null) {
                                handleNewChat(update.message());
                            }
                            handleExistingChat(update.message());
                            response(chat);
                        } catch (Exception e) {
                            Log.e(this.getClass().toString(), this.toString(), e);
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
            }
        });
    }

    private void response(Chat chat) {
        final List<CashState> cashStates = cashStateRepo.findAllByChatId(chat);
        StringBuilder builder = new StringBuilder();
        for (CashState cashState : cashStates) {
            builder.append(gson.toJson(cashState));
        }
        sendMessage(chat.getChatId().toString(), builder.toString());
    }

    private void handleExistingChat(Message message) {
        final Chat chat = chatRepo.findByChatId(message.chat().id());
        final List<CashState> cashStateList = cashStateRepo.findAllByChatId(chat);
        if (cashStateList.isEmpty()) {
            final CashState cashState = new CashState();
            cashState.fill(message);
            cashState.setChat(chat);
            return;
        }
        for (CashState cashState : cashStateList) {
            if (message.from().id().longValue() == cashState.getSpenderId()) {
                cashState.setCashState(cashState.getCashState() + Long.parseLong(message.text()));
            }
        }
    }

    private String handleNewChat(Message message) {
        com.pengrad.telegrambot.model.Chat chatTelegram = message.chat();
        final Chat chat = new Chat();
        chat.setChatId(chatTelegram.id());
        chat.setName(chatTelegram.title());
        return chatRepo.saveChat(chat);
    }

}
