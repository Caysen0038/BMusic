package com.baokaicong.android.bmusic.service.remoter.command;

import com.baokaicong.android.bmusic.service.remoter.RemoteReceiver;

public class NextCommand extends BaseCommand {
    @Override
    protected void work(RemoteReceiver receiver, Object data) {
        receiver.next();
    }
}
