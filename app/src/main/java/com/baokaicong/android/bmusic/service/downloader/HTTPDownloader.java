package com.baokaicong.android.bmusic.service.downloader;

public interface HTTPDownloader extends Downloader<String> {

    void download(String url,String path);

}
