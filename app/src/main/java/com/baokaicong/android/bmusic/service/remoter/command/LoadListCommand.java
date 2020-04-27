package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;

import java.util.List;

public class LoadListCommand extends BaseCommand<List<Music>> {
    @Override
    protected void work(RemoteReceiver receiver,List<Music> data) {
        receiver.load(data);
    }
}
