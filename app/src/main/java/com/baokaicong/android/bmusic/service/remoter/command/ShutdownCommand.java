package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.MediaController;

public class ShutdownCommand extends BaseCommand {
    @Override
    protected void work(MediaController receiver, Object data) {
        receiver.shutdown();
    }
}
