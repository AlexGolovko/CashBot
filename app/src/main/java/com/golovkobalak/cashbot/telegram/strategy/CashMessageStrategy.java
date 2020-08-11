package com.golovkobalak.cashbot.telegram.strategy;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.repo.CashFlow;
import com.golovkobalak.cashbot.repo.CashState;
import com.golovkobalak.cashbot.repo.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CashMessageStrategy extends AbstractMessageStrategy {

    @Override
    public String handle(final Update update) {
        final Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String response = "";
                Chat chat = chatRepo.findByChatId(update.message().chat().id());
                if (chat == null) {
                    handleNewChat(update.message());
                    chat = chatRepo.findByChatId(update.message().chat().id());
                    response = "Hello " + update.message().from().firstName() + " \n" +
                            "I will help U with money saving (no)\n" +
                            "Available commands:\n" +
                            "/clear - clean current spending state.\n" +
                            "/lastmonth - show all spending for last 31 days.\n" +
                            "Your first record:\n";
                }
                handleExistingChat(update.message());

                return response + toResponse(chat);
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Are U sure that is the correct cash amount?";
        }

    }

    private void handleExistingChat(Message message) {
        final Chat chat = chatRepo.findByChatId(message.chat().id());
        final List<CashState> cashStateList = cashStateRepo.findAllByChatId(chat);
        final CashFlow cashFlow = new CashFlow().fill(message);
        cashFlow.setChat(chat);
        cashFlowRepo.save(cashFlow);
        CashState cashStateNew = null;
        for (CashState cashState : cashStateList) {
            if (message.from().id().longValue() == cashState.getSpenderId()) {
                final long cashStateValue = cashState.getCashState() + Long.parseLong(message.text());
                cashStateNew = cashState.copy();
                cashStateNew.setCashState(cashStateValue);
            }
        }
        if (cashStateNew == null) {
            cashStateNew = new CashState();
            cashStateNew.fill(message);
            cashStateNew.setChat(chat);
        }
        cashStateRepo.saveCashState(cashStateNew);
    }

    private String handleNewChat(Message message) {
        com.pengrad.telegrambot.model.Chat chatTelegram = message.chat();
        final Chat chat = new Chat();
        chat.setChatId(chatTelegram.id());
        if (chatTelegram.title() == null) {
            chat.setName(chatTelegram.firstName() + ":" + chatTelegram.lastName());
        }
        chat.setName(chatTelegram.title());
        return chatRepo.saveChat(chat);
    }

    private String toResponse(Chat chat) {
        final List<CashState> cashStates = cashStateRepo.findAllByChatId(chat);
        StringBuilder builder = new StringBuilder();
        for (CashState cashState : cashStates) {
            builder.append(cashState.toMessage()).append('\n');
        }
        if (cashStates.size() == 2) {
            final CashState cashStateOne = cashStates.get(0);
            final CashState cashStateTwo = cashStates.get(1);
            if (cashStateOne.getCashState() > cashStateTwo.getCashState()) {
                builder.append("Delta: ").append(cashStateOne.getSpenderName()).append(':').append(cashStateOne.getCashState() - cashStateTwo.getCashState());
            } else {
                builder.append("Delta: ").append(cashStateTwo.getSpenderName()).append(':').append(cashStateTwo.getCashState() - cashStateOne.getCashState());
            }
        }
        final String response = builder.toString();
        Log.i(Thread.currentThread().getName(), "response: " + response);
        return response;
    }
}
