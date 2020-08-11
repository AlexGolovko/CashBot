package com.golovkobalak.cashbot.repo;

import android.graphics.ComposeShader;
import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.pengrad.telegrambot.model.Message;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.UUID;

public class CashState extends RealmObject {
    public static final String TABLE = "cashState";
    public static final String SPENDER_ID = "spenderId";
    public static final String CASH_STATE = "cashState";
    public static final String SPENDER_NAME = "spenderName";

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Chat chat;
    private Long spenderId;
    private String spenderName;
    private Long cashState;

    public CashState() {
        super();
    }

    private CashState(CashState cashState) {
        this.id = cashState.id;
        this.chat = cashState.chat;
        this.spenderId = cashState.spenderId;
        this.spenderName = cashState.spenderName;
        this.cashState = cashState.cashState;
    }


    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Long getSpenderId() {
        return spenderId;
    }

    public void setSpenderId(Long spenderId) {
        this.spenderId = spenderId;
    }

    public String getSpenderName() {
        return spenderName;
    }

    public void setSpenderName(String spenderName) {
        this.spenderName = spenderName;
    }

    public Long getCashState() {
        return cashState;
    }

    public void setCashState(Long cashState) {
        this.cashState = cashState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void fill(Message message) {
        spenderId = (long) message.from().id();
        spenderName = message.from().firstName();
        if (cashState == null) {
            cashState = Long.valueOf(message.text());
        }
        cashState += Long.valueOf(message.text());
    }

    public CashState copy() {
        return new CashState(this);
    }

    public String toMessage() {
        return this.spenderName + " : " + this.cashState;
    }
}
