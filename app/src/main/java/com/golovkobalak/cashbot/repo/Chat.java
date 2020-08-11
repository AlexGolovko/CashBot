package com.golovkobalak.cashbot.repo;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.UUID;

@RealmClass
public class Chat extends RealmObject {
    public static final String CHAT_ID = "chatId";
    public static final String NAME = "name";

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private Long chatId;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
