package com.golovkobalak.cashbot.repo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;
import java.util.UUID;

public class CashFlow extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Chat chat;
    private String spenderName;
    private String spenderId;
    private int moneySum;
    private Date CreateDate;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getSpenderName() {
        return spenderName;
    }

    public void setSpenderName(String spenderName) {
        this.spenderName = spenderName;
    }

    public String getSpenderId() {
        return spenderId;
    }

    public void setSpenderId(String spenderId) {
        this.spenderId = spenderId;
    }

    public int getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(int moneySum) {
        this.moneySum = moneySum;
    }

    public Date getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
