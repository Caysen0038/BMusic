package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;

public class ShutdownCommand extends BaseCommand {
    @Override
    protected void work(RemoteReceiver receiver,Object data) {
        receiver.shutdown();
    }
}
