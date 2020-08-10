package com.golovkobalak.cashbot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.golovkobalak.cashbot.telegram.CashBot;

public class MainActivity extends AppCompatActivity {
    private static CashBot cashBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cashBot = new CashBot(getApplicationContext());
        cashBot.postConstruct();
    }

}