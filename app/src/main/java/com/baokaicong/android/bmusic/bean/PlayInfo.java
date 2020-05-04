package com.baokaicong.android.bmusic.bean;

import lombok.Data;

@Data
public class PlayInfo {
    private Music currentMusic;
    private int progress;
    private boolean isPlaying;
    private MusicList musicList;
}
