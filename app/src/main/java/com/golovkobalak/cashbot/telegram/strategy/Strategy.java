package com.golovkobalak.cashbot.telegram.strategy;

import com.pengrad.telegrambot.model.Update;

public interface Strategy {
    public String handle(Update update);
}
