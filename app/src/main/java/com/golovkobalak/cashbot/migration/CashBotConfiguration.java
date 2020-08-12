package com.golovkobalak.cashbot.migration;

import com.golovkobalak.cashbot.migration.CashBotMigration;
import io.realm.RealmConfiguration;
import io.realm.RealmConfiguration.Builder;

public class CashBotConfiguration {

    private static final RealmConfiguration config = new Builder()
            .schemaVersion(1)
            .migration(new CashBotMigration())
            .build();

    public static RealmConfiguration getConfig() {
        return config;
    }
}
