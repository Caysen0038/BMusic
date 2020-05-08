package com.baokaicong.android.bmusic.bean;

import androidx.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Music {
    private String mid;
    private String name;
    private String singer;
    private String aldum;
    private int duration;
    private long size;
    private String suffix;
    private String url;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null)
            return false;
        if(!(obj instanceof Music))
            return false;
        return ((Music)obj).getMid().equals(this.mid);
    }
}
