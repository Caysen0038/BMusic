package com.baokaicong.android.bmusic.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DownloadInfo {
    private int id;
    private String mid;
    private String path;
    private String date;
    private String userId;
}
