package com.baokaicong.android.bmusic.bean;

import com.baokaicong.android.bmusic.consts.PlayMode;

import lombok.Data;

@Data
public class PlayInfo {
    private Music currentMusic;
    private int progress;
    private boolean isPlaying;
    private MusicList musicList;
    private PlayMode mode;
}
