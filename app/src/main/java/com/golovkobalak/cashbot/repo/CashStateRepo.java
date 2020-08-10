package com.golovkobalak.cashbot.repo;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;

public class CashStateRepo {
    final Realm realm;

    public CashStateRepo(Context ctx) {
        Realm.init(ctx);
        this.realm = Realm.getDefaultInstance();
    }

    public String saveCashState(CashState cashState) {
        realm.beginTransaction();
        final CashState state = realm.copyToRealmOrUpdate(cashState);
        realm.commitTransaction();
        return state.getId();
    }

    public List<CashState> findAllByChatId(Chat chat) {
        final RealmResults<CashState> all = realm.where(CashState.class).equalTo("chat.id", chat.getId()).sort(CashState.SPENDER_NAME).findAll();
        return new ArrayList<>(all);
    }

}
