package com.baokaicong.android.bmusic.service.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MP3Downloader implements HTTPDownloader {

    private static class Holder{
        private static final MP3Downloader instance=new MP3Downloader();
    }

    public static void downloadMP3(String url,String path){
        Holder.instance.download(url,path);
    }

    @Override
    public void download(String url, String path) {
        URL u= null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection urlConn= null;
        try {
            urlConn = u.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream input= null;
        try {
            input = urlConn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file=new File(path);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream out= null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] data=new byte[1024*128];
        int len;
        try{
            while((len=input.read(data))>0){
                out.write(data,0,len);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
