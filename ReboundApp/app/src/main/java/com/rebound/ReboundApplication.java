package com.rebound;

import android.app.Application;

public class ReboundApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        com.rebound.utils.CartManager.init(this);
        com.rebound.utils.OrderManager.init(this);
    }
}