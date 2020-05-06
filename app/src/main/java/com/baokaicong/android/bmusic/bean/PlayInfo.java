package com.baokaicong.android.bmusic.bean;

import com.baokaicong.android.bmusic.consts.PlayMode;

import java.util.List;

import lombok.Data;

@Data
public class PlayInfo {
    private Music currentMusic;
    private int progress;
    private boolean isPlaying;
    private List<Music> musicList;
    private PlayMode mode;
    private String MusicListId;
}
