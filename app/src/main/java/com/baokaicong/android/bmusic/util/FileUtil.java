package com.baokaicong.android.bmusic.util;

public class FileUtil {
    private static final String[] illegalChar={"/","\\",":","?","*","\"","|","<",">"};
    private static final String[] units={"B","K","M","G","T","P"};

    public static String getFilePrefix(String name){
        int n=name.lastIndexOf(".");
        if(n==-1){
            return null;
        }
        return name.substring(n);
    }

    /**
     * 通过文件长度计算文件大小单位
     * @param size
     * @return
     */
    public static String countFileSize(long size){
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

    /**
     * 判断是否为非法文件名
     * @param fileName
     * @return
     */
    public static final boolean isIllegalFileName(String fileName){
        for(String s:illegalChar){
            if(fileName.contains(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为合法文件名
     * @param fileName
     * @return
     */
    public static final boolean isLegalFileName(String fileName){
        return !isIllegalFileName(fileName);
    }

}
