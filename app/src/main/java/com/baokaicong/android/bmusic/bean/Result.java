package com.baokaicong.android.bmusic.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 标准响应数据Bean
 */
@Data
@Accessors(chain = true)
public class Result<T> {
    // 响应代码
    private String code;
    // 返回值
    private T data;

    private String time;


}
