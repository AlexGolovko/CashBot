package com.golovkobalak.cashbot.repo;

import android.content.Context;
import com.golovkobalak.cashbot.migration.CashBotConfiguration;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;

public class CashStateRepo {
    final Realm realm;

    public CashStateRepo(Context ctx) {
        Realm.init(ctx);
        this.realm = Realm.getInstance(CashBotConfiguration.getConfig());
    }

    public String saveCashState(CashState cashState) {
        realm.beginTransaction();
        final CashState state = realm.copyToRealmOrUpdate(cashState);
        realm.commitTransaction();
        return state.getId();
    }

    public List<CashState> findAllByChatId(Chat chat) {
        final RealmResults<CashState> all = realm.where(CashState.class).equalTo("chat.id", chat.getId()).sort(CashState.SPENDER_NAME).findAll();
        final ArrayList<CashState> cashStates = new ArrayList<>(all.size());
        for (CashState cashState : all) {
            cashStates.add(cashState.copy());
        }
        return cashStates;
    }

}
