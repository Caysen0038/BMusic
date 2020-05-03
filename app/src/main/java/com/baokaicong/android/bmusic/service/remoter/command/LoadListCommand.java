package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class LoadListCommand extends BaseCommand<MusicList> {
    @Override
    protected void work(MediaController receiver, MusicList data) {
        receiver.load(data);
    }
}
