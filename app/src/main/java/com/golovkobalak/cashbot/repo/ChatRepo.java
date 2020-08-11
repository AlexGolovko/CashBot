package com.golovkobalak.cashbot.repo;

import android.content.Context;
import io.realm.Realm;

public class ChatRepo {
    final Realm realm;

    public ChatRepo(Context ctx) {
        Realm.init(ctx);
        this.realm = Realm.getDefaultInstance();
    }

    public ChatRepo(Realm realm) {
        this.realm = realm;
    }

    public String saveChat(Chat chat) {
        realm.beginTransaction();
        Chat chatRealm = realm.copyToRealm(chat);
        realm.commitTransaction();
        return chatRealm.getId();
    }

    public Chat findByChatId(Long chatId) {
        return realm.where(Chat.class).equalTo(Chat.CHAT_ID, chatId).findFirst();
    }
}
