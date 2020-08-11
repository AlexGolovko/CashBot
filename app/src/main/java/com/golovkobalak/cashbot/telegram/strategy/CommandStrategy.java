package com.golovkobalak.cashbot.telegram.strategy;

import android.content.Context;
import com.golovkobalak.cashbot.repo.CashFlow;
import com.golovkobalak.cashbot.repo.CashState;
import com.golovkobalak.cashbot.repo.Chat;
import com.pengrad.telegrambot.model.Update;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CommandStrategy extends AbstractMessageStrategy {

    private static final List<String> CLEAR_COMMANDS = Arrays.asList("/clear", "/clear@LouieHolovkoBot");
    private static final String ALL_CLEAR = "all clear";
    private static final String UNSUPPORTED_COMMAND = "Unsupported yet";
    private static final List<String> LAST_MONTH_COMMANDS = Arrays.asList("/lastmonth", "/lastmonth@LouieHolovkoBot");


    @Override
    public String handle(final Update update) {
        final Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                final Long chatId = update.message().chat().id();
                Chat chat = chatRepo.findByChatId(chatId);
                if (chat == null) {
                    return "chat is not known";
                }
                if (CLEAR_COMMANDS.contains(update.message().text().trim())) {
                    final List<CashState> cashStates = cashStateRepo.findAllByChatId(chat);
                    for (CashState cashState : cashStates) {
                        cashState.setCashState(0L);
                        cashStateRepo.saveCashState(cashState);
                    }
                    return ALL_CLEAR;
                }
                if (LAST_MONTH_COMMANDS.contains(update.message().text().trim())) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -31);
                    final Date date = cal.getTime();
                    final List<CashFlow> cashFlows = cashFlowRepo.findAllByChatAfterDate(chat, date);
                    final StringBuilder builder = new StringBuilder();
                    int sum = 0;
                    for (CashFlow cashFlow : cashFlows) {
                        sum += cashFlow.getMoneySum();
                        builder.append(cashFlow.getCreateDate()).append('\t').append(cashFlow.getSpenderName()).append(':').append(cashFlow.getMoneySum()).append('\n');
                    }
                    builder.append("Result: ").append(sum);
                    return builder.toString();
                }
                return UNSUPPORTED_COMMAND;

            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return EMPTY_STRING;
        }

    }
}
