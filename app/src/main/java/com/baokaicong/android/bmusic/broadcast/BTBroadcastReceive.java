package com.baokaicong.android.bmusic.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.service.remoter.command.PauseCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;

public class BTBroadcastReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
               // Toast.makeText(context , "蓝牙设备:" + device.getName() + "已连接", Toast.LENGTH_SHORT).show();
                if(!BMContext.instance().getPlayInfo().isPlaying() &&
                        BMContext.instance().getPlayInfo().isInterrupt()){
                    BMContext.instance().getPlayInfo().setInterrupt(false);
                    BMContext.instance().getRemoter().command(new PlayCommand(),null);
                }
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
               // Toast.makeText(context , "蓝牙设备:" + device.getName() + "已断开", Toast.LENGTH_SHORT).show();
                if(BMContext.instance().getPlayInfo().isPlaying()){
                    BMContext.instance().getPlayInfo().setInterrupt(true);
                    BMContext.instance().getRemoter().command(new PauseCommand(),null);
                }
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState){
                    case BluetoothAdapter.STATE_OFF:
                        //Toast.makeText(context , "蓝牙已关闭", Toast.LENGTH_SHORT).show();

                        break;
                    case BluetoothAdapter.STATE_ON:
                        //Toast.makeText(context , "蓝牙已开启"  , Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
}
