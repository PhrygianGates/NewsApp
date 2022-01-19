package com.java.xiongzhicheng;

import android.content.Context;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyApplication extends LitePalApplication {
    static Context context;
    static HomeFragment homeFragment;
    static HistoryFragment historyFragment;
    static MarkFragment markFragment;
    static List<String> chosen;
    static List<String> unchosen;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        markFragment = new MarkFragment();
        chosen = new ArrayList<>(Arrays.asList("娱乐", "军事", "财经", "科技"));
        unchosen = new ArrayList<>(Arrays.asList("教育", "文化", "健康", "体育", "汽车", "社会"));
    }
}
