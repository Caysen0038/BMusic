package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class JumpCommand extends BaseCommand<Integer> {

    @Override
    protected void work(MediaController receiver, Integer data) {
        receiver.jump(data);
    }
}
