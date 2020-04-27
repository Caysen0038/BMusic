package com.baokaicong.android.bmusic.service.binder;

import android.app.Service;
import android.os.Binder;

public class CustomBinder extends Binder {
    private Service service;
    public <T extends Service> CustomBinder(T s){
        this.service=s;
    }

    public <T> T getService(){
        return (T)this.service;
    }
}
