package com.golovkobalak.cashbot.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static android.provider.BaseColumns._ID;
import static com.golovkobalak.cashbot.repo.CashEngineDB.CashFlow.*;
import static com.golovkobalak.cashbot.repo.CashEngineDB.Chat.CHAT_ID;


public class CashEngineDB extends SQLiteOpenHelper {

    public CashEngineDB(Context context) {
        super(context, "CashDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Chat.TABLE + " (" +
                _ID + " integer primary key autoincrement, " +
                CHAT_ID + " integer," +
                "name TEXT" +
                ")");

        db.execSQL("create table " + CashFlow.TABLE + "(" +
                _ID + " integer primary key autoincrement," +
                SPENDER_NAME + " text," +
                SPENDER_ID + " text," +
                MONEY_SUM + " integer," +
                CREATE_DATE + " date," +
                "FOREIGN KEY(" + CHAT_ID + ") REFERENCES " + CashFlow.TABLE + "(" + _ID + ")" +
                ")");
        db.execSQL("create table CASH_STATE (" +
                "_id integer primary key autoincrement, " +
                "SPENDER_ID text," +
                "SPENDER_NAME text," +
                "CASH_STATE integer," +
                "FOREIGN KEY (CHAT_ID)REFERENCES cashflow(_id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Chat.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CashFlow.TABLE);
        onCreate(db);
    }

    public static class Chat implements BaseColumns {
        public static final String TABLE = "chat";
        public static final String CHAT_ID = "chat_id";
    }

    public static class CashFlow implements BaseColumns {
        public static final String TABLE = "cash_flow";
        public static final String SPENDER_NAME = "spender_name";
        public static final String SPENDER_ID = "spender_id";
        public static final String MONEY_SUM = "money_sum";
        public static final String CREATE_DATE = "CREATE_DATE";
    }

    public static class CashState implements BaseColumns {
        public static final String TABLE = "CASH_STATE";
        public static final String SPENDER_ID = "SPENDER_ID";
        public static final String CASH_STATE = "CASH_STATE";
        public static final String SPENDER_NAME = "SPENDER_NAME";
    }
}
