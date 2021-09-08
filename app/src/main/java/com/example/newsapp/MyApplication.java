package com.example.newsapp;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DataSupportException;

import java.util.List;

public class MyApplication extends LitePalApplication {
    static Context context;
    static HistoryFragment historyFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        historyFragment = new HistoryFragment();
    }
}
