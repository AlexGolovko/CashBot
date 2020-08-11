package com.golovkobalak.cashbot.telegram;

import android.content.Context;
import android.util.Log;
import com.golovkobalak.cashbot.repo.*;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdatesListener implements com.pengrad.telegrambot.UpdatesListener {
    private CashStateRepo cashStateRepo;
    private ChatRepo chatRepo;
    private CashFlowRepo cashFlowRepo;
    private final ExecutorService executorService;
    private CashBot bot;

    public UpdatesListener(final Context context) {
        executorService = Executors.newSingleThreadExecutor();

        final Future<?> submit = executorService.submit(new Runnable() {
            @Override
            public void run() {
                chatRepo = new ChatRepo(context);
                cashStateRepo = new CashStateRepo(context);
                cashFlowRepo = new CashFlowRepo(context);

            }
        });
        try {
            submit.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int process(List<Update> updates) {
        {
            for (final Update update : updates) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Long chatTelegramId = update.message().chat().id();
                            Chat chat = chatRepo.findByChatId(chatTelegramId);
                            if (chat == null) {
                                handleNewChat(update.message());
                            }
                            handleExistingChat(update.message());
                            response(chat);
                        } catch (Exception e) {
                            Log.e(this.getClass().toString(), this.toString(), e);

                        }
                    }
                });

            }
            return CONFIRMED_UPDATES_ALL;
        }
    }

    private void response(Chat chat) {
        final List<CashState> cashStates = cashStateRepo.findAllByChatId(chat);
        StringBuilder builder = new StringBuilder();
        for (CashState cashState : cashStates) {
            builder.append(cashState.toMessage()).append('\n');
        }
        Log.i(Thread.currentThread().getName(), "response: " + builder.toString());
        bot.sendMessage(chat.getChatId().toString(), builder.toString());
    }

    private void handleExistingChat(Message message) {
        final Chat chat = chatRepo.findByChatId(message.chat().id());
        final List<CashState> cashStateList = cashStateRepo.findAllByChatId(chat);
        final CashFlow cashFlow = new CashFlow().fill(message);
        cashFlow.setChat(chat);
        cashFlowRepo.save(cashFlow);
        if (cashStateList.isEmpty()) {
            final CashState cashState = new CashState();
            cashState.fill(message);
            cashState.setChat(chat);
            cashStateRepo.saveCashState(cashState);
            return;
        }
        for (CashState cashState : cashStateList) {
            if (message.from().id().longValue() == cashState.getSpenderId()) {
                final long cashStateNew = cashState.getCashState() + Long.parseLong(message.text());
                final CashState copy = cashState.copy();
                copy.setCashState(cashStateNew);
                cashStateRepo.saveCashState(copy);
            }
        }
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

    public void setBot(CashBot bot) {
        this.bot = bot;
    }
}
