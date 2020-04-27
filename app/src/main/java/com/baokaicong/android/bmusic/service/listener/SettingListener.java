package com.baokaicong.android.bmusic.service.listener;

public interface SettingListener {

    void onSettingUpdated(String name,String value);
    void onSettingCreated(String name,String value);
}
