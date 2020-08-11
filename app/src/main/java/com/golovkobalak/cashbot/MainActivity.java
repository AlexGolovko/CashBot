package com.golovkobalak.cashbot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.golovkobalak.cashbot.telegram.CashBot;
import com.golovkobalak.cashbot.telegram.UpdatesListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static CashBot cashBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        cashBot = new CashBot(getApplicationContext());
        final UpdatesListener updatesListener = new UpdatesListener(getApplicationContext());
        cashBot.setUpdatesListener(updatesListener);

    }

}