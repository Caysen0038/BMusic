package com.baokaicong.android.bmusic.ui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.service.remoter.command.Command;
import com.baokaicong.android.bmusic.service.remoter.command.NextCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PauseCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PreCommand;
import com.baokaicong.android.bmusic.util.ToastUtil;

public class RemoteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String cmd=intent.getStringExtra("remote");

        if(cmd!=null){
            Command command=null;
            switch (cmd){
                case "next":
                    command=new NextCommand();
                    break;
                case "pre":
                    command=new PreCommand();
                    break;
                case "play":
                    if(BMContext.instance().getPlayInfo().isPlaying()){
                        command=new PauseCommand();
                    }else{
                        command=new PlayCommand();
                    }

                    break;
            }
            BMContext.instance().getRemoter().command(command,null);
        }

        abortBroadcast();
    }
}
