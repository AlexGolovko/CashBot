package com.golovkobalak.cashbot.repo;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CashFlowRepo {
    private static final String ERROR_TEXT = "cashFlow cannot be null";
    final Realm realm;

    public CashFlowRepo(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public CashFlowRepo(Realm realm) {
        this.realm = realm;
    }

    public String save(CashFlow cashFlow) {
        if (cashFlow == null) {
            throw new IllegalArgumentException(ERROR_TEXT);
        }
        if (cashFlow.getCreateDate() == null) {
            cashFlow.setCreateDate(new Date());
        }
        realm.beginTransaction();
        CashFlow flow = realm.copyToRealmOrUpdate(cashFlow);
        realm.commitTransaction();
        return flow.getId();
    }

    public List<CashFlow> findAllByChat(Chat chat) {
        RealmResults<CashFlow> cashFlows = realm.where(CashFlow.class).equalTo("chat.id", chat.getId()).findAll();
        return Collections.unmodifiableList(new ArrayList<>(cashFlows));
    }

    public List<CashFlow> findAllByChatAfterDate(Chat chat, Date date) {
        return realm.where(CashFlow.class).equalTo("chat.id", chat.getId()).between("createDate", date, new Date()).sort("createDate", Sort.DESCENDING).findAll();
    }
}
