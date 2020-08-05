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
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.golovkobalak.cashbot.repo.CashEngineDB.Chat.CHAT_ID;

public class CashBot {
    private final TelegramBot bot;
    private final SQLiteDatabase readableDB;
    private final SQLiteDatabase writableDB;

    public CashBot(Context context) {
        CashEngineDB db = new CashEngineDB(context);
        readableDB = db.getReadableDatabase();
        writableDB = db.getWritableDatabase();
        String token = context.getString(R.string.bot_token);
        bot = new TelegramBot(token);
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
                            } else {
                                final ContentValues chat = new ContentValues();
                                chat.put(CHAT_ID, chatTelegramId);
                                chatId = writableDB.insert(Chat.TABLE, null, chat);
                            }
                            cursor.close();
                            Cursor cashFlow = readableDB.rawQuery("select * from cash_flow where chat_id=? order by CREATE_DATE desc", new String[]{String.valueOf(chatId)});
                            if (cashFlow.moveToFirst()) {

                            } else {
                                final ContentValues cashFlowEntity = new ContentValues();
                                cashFlowEntity.put(CashEngineDB.CashFlow.SPENDER_ID, update.message().from().id());
                                cashFlowEntity.put(CashEngineDB.CashFlow.SPENDER_NAME, update.message().from().firstName());
                                cashFlowEntity.put(CashEngineDB.CashFlow.MONEY_SUM, update.message().text());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                Date date = new Date();
                                cashFlowEntity.put(CashEngineDB.CashFlow.CREATE_DATE, dateFormat.format(date));

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

}
