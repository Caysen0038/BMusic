package com.baokaicong.android.bmusic.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadEngine {

    public static void download(String url,String path) throws IOException {
        URL u= null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection urlConn=u.openConnection();
        InputStream input=urlConn.getInputStream();
        File file=new File(path);
        if(!file.exists()){
            //file.getParentFile().mkdirs();
            file.createNewFile();
        }
        OutputStream out=new FileOutputStream(file);
        byte[] data=new byte[1024];
        int len;
        while((len=input.read(data))>0){
            out.write(data,0,len);
        }
        input.close();
        out.close();
    }
}
