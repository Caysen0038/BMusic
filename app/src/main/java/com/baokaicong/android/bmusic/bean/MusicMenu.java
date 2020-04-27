package com.baokaicong.android.bmusic.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicMenu {
    private String meid;
    private String img;
    private String name;
    private String owner;
    private String date;

}
