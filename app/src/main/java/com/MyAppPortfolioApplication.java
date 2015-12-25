package com;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by maulin on 17/12/15.
 */
public class MyAppPortfolioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
