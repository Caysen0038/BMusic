package com.baokaicong.android.bmusic.service.worker;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.downloader.MP3Downloader;

public class MusicDownloadWorker extends AsyncWorker<Music> {
    private String path;
    public MusicDownloadWorker(){

    }

    public void download(Music music,String path){
        this.path=path;
        start(music);
    }

    @Override
    protected Music doInBackground(Music... music) {
        if(music!=null){
            notifyStart(music[0]);
            for(Music m:music){
                MP3Downloader.downloadMP3(m.getUrl(),path);
            }
        }
        notifyComplete(music[0]);
        return music[0];
    }

    @Override
    protected void onProgressUpdate(Integer... progresses) {
        reportProgress(progresses[0]);
    }


    @Override
    protected void onCancelled(Music music) {
        super.onCancelled(music);
    }

    @Override
    public void start(Music param) {
        execute(param);
    }

    @Override
    public void cancel() {

    }


}
