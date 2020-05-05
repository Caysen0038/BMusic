package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.consts.PlayMode;
import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class PlayModeCommand extends BaseCommand<PlayMode> {
    @Override
    protected void work(MediaController receiver, PlayMode data) {
        if(data!=null){
            receiver.playMode(data);
        }
    }
}
