package com.baokaicong.android.bmusic.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class Page {
    private int current;
    private int total;
    private int per;
}
