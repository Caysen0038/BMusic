package com.baokaicong.android.bmusic.service.listener;

import com.baokaicong.android.bmusic.bean.Music;

public interface GlobalMusicPlayListener {
    String NAME="MUSIC_PLAY";
    void onPlay(Music music);
    void onPause(Music music);
    void onSwitch(Music music);
    void progress(int p);
}
