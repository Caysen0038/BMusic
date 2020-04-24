package com.baokaicong.android.bmusic.service.remoter;

import java.util.List;

public interface RemoteReceiver<T> {
    void load(List<T> media);
    void add(T m,int i);
    void play();
    void pause();
    void next();
    void pre();
    void jump(int rate);
}
