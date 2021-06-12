package com.rupeek.rupeektest;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class RupeekApp extends MultiDexApplication {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
