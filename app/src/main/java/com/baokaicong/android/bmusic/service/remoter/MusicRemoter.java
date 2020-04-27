package com.baokaicong.android.bmusic.service.remoter;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.command.Command;

import java.util.ArrayList;
import java.util.List;

public class MusicRemoter implements MediaRemoter<Music> {
    private RemoteReceiver<Music> receiver;
    public MusicRemoter(RemoteReceiver<Music> receiver){
        this.receiver=receiver;
    }


    @Override
    public <B> void command(Command command,B data) {
        command.execute(this.receiver,data);
    }
}
