package com.golovkobalak.cashbot.telegram;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.R;
import com.golovkobalak.cashbot.repo.*;
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
    private final ChatRepo chatRepo;

    public CashBot(Context context) {

        String token = context.getString(R.string.bot_token);
        bot = new TelegramBot(token);
        chatRepo = new ChatRepo(context);
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
                                return UpdatesListener.CONFIRMED_UPDATES_ALL;
                            }
                            // TODO handleExistingChat(update.message());
                        } catch (Exception e) {
                            Log.e(this.getClass().toString(), this.toString(), e);
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
            }
        });
    }


    private String handleNewChat(Message message) {
        com.pengrad.telegrambot.model.Chat chatTelegram = message.chat();

        final Chat chat = new Chat();
        chat.setChatId(chatTelegram.id());
        chat.setName(chatTelegram.title());
        return chatRepo.saveChat(chat);
    }

}
