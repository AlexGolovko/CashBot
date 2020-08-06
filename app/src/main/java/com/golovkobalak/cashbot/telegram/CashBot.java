package com.golovkobalak.cashbot.telegram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.golovkobalak.cashbot.R;
import com.golovkobalak.cashbot.repo.CashEngineDB;
import com.golovkobalak.cashbot.repo.CashEngineDB.Chat;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.golovkobalak.cashbot.repo.CashEngineDB.CashFlow.*;
import static com.golovkobalak.cashbot.repo.CashEngineDB.Chat.CHAT_ID;

public class CashBot {
    private final TelegramBot bot;
    private final SQLiteDatabase readableDB;
    private final SQLiteDatabase writableDB;
    private final SimpleDateFormat dateFormat;

    public CashBot(Context context) {
        CashEngineDB db = new CashEngineDB(context);
        readableDB = db.getReadableDatabase();
        writableDB = db.getWritableDatabase();
        String token = context.getString(R.string.bot_token);
        bot = new TelegramBot(token);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
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
                            Cursor cursor = readableDB.rawQuery("select " + Chat._ID + " from chat where " + CHAT_ID + "=?", new String[]{String.valueOf(chatTelegramId)});
                            final long chatId;
                            if (cursor.moveToFirst()) {
                                chatId = cursor.getLong(0);
                                cursor.close();
                            } else {
                                handleNewChat(update.message());
                                return UpdatesListener.CONFIRMED_UPDATES_ALL;
                            }
                            Cursor cashFlow = readableDB.rawQuery("select * from cash_flow where chat_id=? order by CREATE_DATE desc", new String[]{String.valueOf(chatId)});
                            if (cashFlow.moveToFirst()) {

                            } else {
                                final ContentValues cashFlowEntity = new ContentValues();
                                cashFlowEntity.put(SPENDER_ID, update.message().from().id());
                                cashFlowEntity.put(SPENDER_NAME, update.message().from().firstName());
                                cashFlowEntity.put(CashEngineDB.CashFlow.MONEY_SUM, update.message().text());
                                cashFlowEntity.put(CashEngineDB.CashFlow.CREATE_DATE, dateFormat.format(new Date()));

                            }
                        } catch (Exception e) {
                            Log.e(this.toString(), e.getMessage());
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
            }
        });
    }

    private void handleNewChat(Message message) {
        final ContentValues chat = new ContentValues();
        chat.put(CHAT_ID, message.chat().id());
        writableDB.insert(Chat.TABLE, null, chat);
        chat.clear();
        ContentValues cash_flow = new ContentValues();
        Integer userID = message.from().id();
        String username = message.from().username();
        Long moneySum = 0l;
        try {
            moneySum = Long.valueOf(message.text());
        } catch (NumberFormatException e) {
            Log.e(this.getClass().toString(), message.text(), e);
        }
        cash_flow.put(SPENDER_ID, userID);
        cash_flow.put(SPENDER_NAME, username);
        cash_flow.put(MONEY_SUM, moneySum);
        cash_flow.put(CashEngineDB.CashFlow.CREATE_DATE, dateFormat.format(new Date()));
        writableDB.insert(CashEngineDB.CashFlow.TABLE, null, cash_flow);
    }

}
