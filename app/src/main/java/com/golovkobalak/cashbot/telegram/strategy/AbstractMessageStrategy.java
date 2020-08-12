package com.golovkobalak.cashbot.telegram.strategy;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.repo.CashFlowRepo;
import com.golovkobalak.cashbot.repo.CashStateRepo;
import com.golovkobalak.cashbot.repo.ChatRepo;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMessageStrategy implements Strategy {
    protected static CashStateRepo cashStateRepo;
    protected static ChatRepo chatRepo;
    protected static CashFlowRepo cashFlowRepo;
    protected static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    protected static final String EMPTY_STRING = "";

    public static void init(final Context context) {
        if (cashFlowRepo == null) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        chatRepo = new ChatRepo(context);
                        cashStateRepo = new CashStateRepo(context);
                        cashFlowRepo = new CashFlowRepo(context);
                    } catch (Exception e) {
                        Log.e(this.getClass().toString(), this.toString(), e);
                    }
                }
            });

        }
    }
}
