package com.baokaicong.android.bmusic.service.remoter;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.command.Command;

public class MusicRemoter implements MediaRemoter<Music> {
    private MediaController<Music> receiver;
    public MusicRemoter(MediaController<Music> receiver){
        this.receiver=receiver;
    }


    @Override
    public <B> void command(Command command,B data) {
        command.execute(this.receiver,data);
    }
}
