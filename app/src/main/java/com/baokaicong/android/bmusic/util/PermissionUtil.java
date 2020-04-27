package com.baokaicong.android.bmusic.util;

import android.content.Context;

import com.yanzhenjie.permission.AndPermission;


public class PermissionUtil {

    private PermissionUtil(){

    }
    private static class Holder{
        private static PermissionUtil instance=new PermissionUtil();
    }

    public static PermissionUtil Instance(){
        return Holder.instance;
    }

    public PermissionUtil request(Context context, String... permission){
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
