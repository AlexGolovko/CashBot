package com.golovkobalak.cashbot.repo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.UUID;

public class CashState extends RealmObject {
    public static final String TABLE = "CASH_STATE";
    public static final String SPENDER_ID = "SPENDER_ID";
    public static final String CASH_STATE = "CASH_STATE";
    public static final String SPENDER_NAME = "SPENDER_NAME";

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Chat chat;
    private Long spenderId;
    private String spenderName;
    private Long cashState;


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
}
