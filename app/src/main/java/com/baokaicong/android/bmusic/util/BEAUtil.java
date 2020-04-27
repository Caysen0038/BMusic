package com.baokaicong.android.bmusic.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class BEAUtil {

    /**
     * BKC式AES加密算法
     * @param content
     * @param key
     * @returnA
     */
    public static String BEA(String content,String key){
        byte[] secret=BEAHelper.getSecretKey(key);
        byte[] data=null;
        try {
            data=content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**
         * data中每10个数据按照key的数字进行交换
         */
        for(int j=0,m=data.length/10;j<m;j++){
            for(int i=0,n=secret.length-1;i<n;i++){
                byte temp=data[secret[i]+j*10];
                data[secret[i]+j*10]=data[secret[i+1]+j*10];
                data[secret[i+1]+j*10]=temp;
            }
        }
        StringBuilder sb=new StringBuilder();
        int m=0;
        for(int i=0;i<data.length;i++){
            int temp=(int)data[i];
            if(BEAHelper.isNumber(temp) || BEAHelper.isAlphabet(temp)){
                // 字母或数字直接添加
                sb.append((char)data[i])
                        .append((char)BEAHelper.getRandomInt());
            }else{
                // 非字母或数字转换后添加
                int n=0;
                int base=-1;
                if(temp<65){
                    base=1;
                }
                int num;
                while(!BEAHelper.isAlphabet(temp)){
                    num=secret[m%secret.length]+10;
                    temp+=num*base;
                    n+=base;
                }
                m++;
                sb.append((char)temp).append((char)BEAHelper.getAlphabet(n));
            }
        }
        return sb.toString();

    }

    /**
     * BKC式AES加密的解密算法
     * @param content
     * @param key
     * @return
     */
    public static String DBEA(String content,String key){
        byte[] secret=BEAHelper.getSecretKey(key);
        byte[] data=null;
        try {
            data=content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int m=0;
        byte[] temp=new byte[data.length/2];
        int k=0;
        for(int i=0;i<data.length;i+=2){
            int n=data[i+1];
            if(n<85 || n>103){
                n=BEAHelper.getNumber(n)*-1;
                int num=secret[m%secret.length]+10;
                temp[k++]=(byte)(data[i]+num*n);
                m++;
            }else{
                temp[k++]=data[i];
            }
        }
        try {
            for(int j=temp.length/10-1;j>=0;j--){
                for(int i=secret.length-1;i>0;i--){
                    byte b=temp[secret[i]+j*10];
                    temp[secret[i]+j*10]=temp[secret[i-1]+j*10];
                    temp[secret[i-1]+j*10]=b;
                }
            }
            return new String(temp,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class BEAHelper{
        private static int getAlphabet(int n){
            return n<0?122+n:65+n;
        }
        private static int getNumber(int n){
            return n<86?n-65:n-122;
        }
        private static int getRandomInt(){
            Random random=new Random();
            if(random.nextInt(2)==0){
                return 85+random.nextInt(6);
            }
            return 97+random.nextInt(6);
        }

        private static byte[] getSecretKey(String key){
            StringBuilder sb=new StringBuilder();
            for(char c:key.toCharArray()){
                sb.append((int)c);
            }
            byte[] s=new byte[sb.length()];
            for(int i=0;i<sb.length();i++){
                s[i]=(byte)Integer.parseInt(sb.toString().substring(i,i+1));
            }
            return s;
        }



        private static boolean isNumber(int n){
            return n>=48&&n<=57;
        }

        private static boolean isAlphabet(int n){
            return (n>=65&&n<=90)||(n>=97&&n<=122);
        }
    }
}
