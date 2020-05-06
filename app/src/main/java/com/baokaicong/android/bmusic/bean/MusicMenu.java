package com.baokaicong.android.bmusic.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MusicMenu {
    private String meid;
    private String img;
    private String name;
    private String owner;
    private String date;
    private int count;
}
