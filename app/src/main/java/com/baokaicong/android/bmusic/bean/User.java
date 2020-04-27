package com.baokaicong.android.bmusic.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private String token;
    private String id;
    private String name;
    private String password;
    private AccountInfo userInfo;
}
