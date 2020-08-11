package com.golovkobalak.cashbot;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.golovkobalak.cashbot.repo.CashFlow;
import com.golovkobalak.cashbot.repo.CashFlowRepo;
import com.golovkobalak.cashbot.repo.Chat;
import com.golovkobalak.cashbot.repo.ChatRepo;
import com.golovkobalak.cashbot.telegram.CashBot;
import com.golovkobalak.cashbot.telegram.UpdatesListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.golovkobalak.cashbot", appContext.getPackageName());
    }

    @Test
    public void realmRelationsTest() throws IOException {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File tempFolder = testFolder.newFolder("realmdata");
        Realm.init(context);
        RealmConfiguration testConfig =
                new RealmConfiguration.Builder().
                        directory(tempFolder).
                        inMemory().
                        name("test.realm").build();
        final Realm realm = Realm.getInstance(testConfig);
        ChatRepo chatRepo = new ChatRepo(realm);
        Long chatID = 1L;
        String chatName = "TestChat";
        final Chat chat = new Chat();
        chat.setName(chatName);
        chat.setChatId(chatID);
        final String saveChat = chatRepo.saveChat(chat);
        final Chat foundChat = chatRepo.findByChatId(chatID);
        assertNotNull(foundChat);
        assertEquals(chat.getName(), foundChat.getName());
        assertEquals(chat.getChatId(), foundChat.getChatId());
        assertEquals(saveChat, foundChat.getId());
        final CashFlow cashFlowOne = new CashFlow();
        cashFlowOne.setChat(chat);
        cashFlowOne.setMoneySum(10);
        String alex = "Alex";
        cashFlowOne.setSpenderName(alex);
        CashFlowRepo cashFlowRepo = new CashFlowRepo(realm);
        final CashFlow cashFlowTwo = new CashFlow();
        cashFlowTwo.setChat(chat);
        String sonya = "Sonya";
        cashFlowTwo.setSpenderName(sonya);
        cashFlowTwo.setMoneySum(20);
        final CashFlow cashFlowThree = new CashFlow();
        cashFlowThree.setSpenderName("Ignat");
        cashFlowRepo.save(cashFlowOne);
        cashFlowRepo.save(cashFlowTwo);
        cashFlowRepo.save(cashFlowThree);
        final List<CashFlow> allByChat = cashFlowRepo.findAllByChat(chat);
        assertEquals(2, allByChat.size());
    }
}