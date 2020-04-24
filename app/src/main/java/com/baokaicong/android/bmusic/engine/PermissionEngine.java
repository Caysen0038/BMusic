package com.baokaicong.android.bmusic.engine;

import android.content.Context;

import com.yanzhenjie.permission.AndPermission;


public class PermissionEngine {

    private PermissionEngine(){

    }
    private static class Holder{
        private static PermissionEngine instance=new PermissionEngine();
    }

    public static PermissionEngine Instance(){
        return Holder.instance;
    }

    public PermissionEngine request(Context context,String... permission){
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .onGranted(list->{

                })
                .onDenied(data->{

                }).start();
        return this;
    }
}
