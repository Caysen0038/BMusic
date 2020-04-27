package com.baokaicong.android.bmusic.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageUtil {

    /**
     * 将指定内容转换为MD5码
     * @param value 消息内容
     * @return 转换后的MD5码，若失败则为null
     */
    public static String MD5(String value){
        try {
            return MD5(value.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 将指定内容转换为MD5码
     * @param value 消息内容
     * @param encode 指定消息的编码
     * @return 转换后的MD5码，若失败则为null
     */
    public static String MD5(String value,String encode){
        try {
            return MD5(value.getBytes(encode));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 将指定内容转换为MD5码
     * @param data 二进制消息数据
     * @return 转换后的MD5码，若失败则为null
     */
    public static String MD5(byte[] data){
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        if(m==null){
            return null;
        }
        byte[] md5=m.digest(data);
        String result = "";
        for (int i = 0; i < md5.length; i++) {
            result += Integer.toHexString((0x000000FF & md5[i]) | 0xFFFFFF00).substring(6);
        }
        return result;
    }

}
