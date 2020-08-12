package com.golovkobalak.cashbot.repo;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class CashFlowTest {

    private final CashFlow cashFlow;
    @Mock
    private Message message;
    @Mock
    private User user;
    private final int moneySum;
    private final String comment;

    public CashFlowTest(String messageText, int moneySum, String comment) {
        this.moneySum = moneySum;
        this.comment = comment;
        initMocks(this);
        cashFlow = new CashFlow();
        doReturn(user).when(message).from();
        doReturn("spenderName").when(user).firstName();
        doReturn(123123).when(user).id();
        doReturn(messageText).when(message).text();
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"12 Text", 12, "Text"}, {"12 ", 12, null}, {" 3 Text 11", 3, "Text 11"}
        });
    }

    @Test
    public void fill() {
        cashFlow.fill(message);
        assertEquals(moneySum, cashFlow.getMoneySum());
        assertEquals(comment, cashFlow.getComment());
    }

}