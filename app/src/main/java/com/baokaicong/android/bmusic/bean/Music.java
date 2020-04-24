package com.baokaicong.android.bmusic.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Music {
    private String mid;
    private String name;
    private String songer;
    private String aldum;
    private int duration;
    private String suffix;
    private String url;

}
