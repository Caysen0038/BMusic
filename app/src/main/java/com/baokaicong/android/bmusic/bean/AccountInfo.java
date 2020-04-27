package com.baokaicong.android.bmusic.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountInfo {
    private long id;
    private String accountId;
    private String name;
}
