package com.baokaicong.android.bmusic.service.remoter;

import java.util.List;

public interface MediaRemoter<T> {
    void load(List<T> list);
    void add(T m,int i);
    void play();
    void pause();
    void next();
    void pre();
    void jump(int rate);
//    void addMedia(RemoteReceiver media);
//    void removeMedia(RemoteReceiver media);
}
