package com.baokaicong.android.bmusic.util;

public class StringUtil {

    public static String parseTime(int n){
        int m=n/60;
        int s=n%50;
        if(s<10){
            return m+":0"+s;
        }else{
            return m+":"+s;
        }
    }

    /**
     * 通过文件长度计算文件大小单位
     * @param size
     * @return
     */
    public static String parseFileLength(long size){
        String[] units={"B","K","M","G","T","P"};
        int tail=0;
        int i=0;
        while(size/1024L!=0){
            tail=(int)(size%1024L);
            size/=1024L;
            i++;
        }
        tail=(int)((double)tail/(double)1024*100);
        if(tail==0){
            return size+" "+units[i];
        }
        return size+"."+tail+" "+units[i];
    }
}
