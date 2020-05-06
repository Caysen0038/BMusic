package com.baokaicong.android.bmusic.bean;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicSearchData {
    private List<Music> musics;
    private Page page;
}
