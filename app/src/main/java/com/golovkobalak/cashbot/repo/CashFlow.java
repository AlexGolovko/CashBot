package com.golovkobalak.cashbot.repo;

import com.pengrad.telegrambot.model.Message;
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
    private Date createDate;

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
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CashFlow fill(Message message) {
        this.spenderName = message.from().firstName();
        this.spenderId = String.valueOf(message.from().id());
        this.moneySum = Integer.parseInt(message.text());
        this.createDate = new Date();
        return this;
    }
}
