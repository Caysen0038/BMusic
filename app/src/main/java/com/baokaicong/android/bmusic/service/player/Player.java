package com.baokaicong.android.bmusic.service.player;

public interface Player<T> {

    boolean loadMedia(T src,boolean loop);
    boolean play();
    void pause();
    void playComplete();
    void release();
    boolean isPlaying();
    boolean jump(int rate);
}
