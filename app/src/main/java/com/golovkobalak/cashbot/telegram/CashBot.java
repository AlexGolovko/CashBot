package com.golovkobalak.cashbot.telegram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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

import static com.golovkobalak.cashbot.repo.CashEngineDB.Chat.CHAT_ID;

public class CashBot {
    public long chatId;
    private final String token = "1200817060:AAHVr7MSir1CwPIpJ99XLAksY61JrxqLUWs";
    private TelegramBot bot = new TelegramBot(token);
    private Context context;
    private final CashEngineDB db;
    private final SQLiteDatabase readableDB;
    private final SQLiteDatabase writableDB;

    public CashBot(Context context) {
        this.context = context;
        db = new CashEngineDB(context);
        readableDB = db.getReadableDatabase();
        writableDB = db.getWritableDatabase();
    }

    public void sendMessage(String s) {
        bot.execute(new GetChat(chatId));
        bot.execute(new SendMessage(chatId, s));
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
                            String message = update.message().text();
                            Cursor cashFlow = readableDB.rawQuery("select * from cash_flow where chat_id=? order by CREATE_DATE desc", new String[]{String.valueOf(chatId)});
                            if (cashFlow.moveToFirst()) {

                            } else {
                                final ContentValues cashFlowEntity = new ContentValues();
                                cashFlowEntity.put(CashEngineDB.CashFlow.SPENDER_ID, update.message().from().id());
                                cashFlowEntity.put(CashEngineDB.CashFlow.SPENDER_NAME, update.message().from().firstName());
                                cashFlowEntity.put(CashEngineDB.CashFlow.MONEY_SUM, update.message().text());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
