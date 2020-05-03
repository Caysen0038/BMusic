package com.baokaicong.android.bmusic.service.remoter.command;


import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class InsertCommand extends BaseCommand<Integer>  {
    private Music music;
    public InsertCommand(Music music){
        this.music=music;
    }

    @Override
    protected void work(MediaController receiver, Integer data) {

        receiver.add(receiver,data);
    }
}
