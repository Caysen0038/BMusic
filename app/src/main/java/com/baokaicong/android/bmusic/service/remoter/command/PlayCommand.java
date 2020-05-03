package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class PlayCommand extends BaseCommand {
    @Override
    protected void work(MediaController receiver, Object data) {
        if(data==null){
            receiver.play();
        }else if(data instanceof Music){
            receiver.play((Music)data);
        }else{
            receiver.play((int)data);
        }

    }
}
