package com.baokaicong.android.bmusic.service.downloader;

public interface Downloader<T> {
    public void download(T src,String path);
    interface DownloadListener{
        void onStart();
        void progress(int rate);
        void onComplete();
        void onError();
    }
}
