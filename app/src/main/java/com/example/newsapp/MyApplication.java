package com.example.newsapp;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
    }
}
