package com.baokaicong.android.bmusic.service.player;

import com.baokaicong.android.bmusic.bean.Music;

public interface MusicPlayer extends Player<Music>{
    interface MusicPlayerListner{
        void onLoadComplete();
        void onPlay();
        void onPause();
        void onPlayComplete();
        void onRelease();
        void onError();
    }
    void addMusicPlayerListner(MusicPlayerListner listner);
    void removeMusicPlayerListner(MusicPlayerListner listner);
}
