package com.baokaicong.android.bmusic.util;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.baokaicong.android.bmusic.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {
    private static final String CHANNEL_NAME="bkcm";
    private static final String CHANNLE_ID="bkcm_";
    private int idcount= R.drawable.icon_bkc_trans_30;
    private Context context;
    private int importance=NotificationManager.IMPORTANCE_LOW;
    private String title="";
    private String text="";
    private int iconId=0x00;
    private int bigIconId=iconId;
    private Notification notification;
    private List<Integer> flagList;
    private NotificationUtil(){
        flagList=new ArrayList<>();
    }
    private static class Holder{
        private static NotificationUtil instance=new NotificationUtil();
    }



    public static NotificationUtil Instance(){
        return Holder.instance;
    }

    public NotificationUtil init(Context context){
        this.context=context;
        flagList.clear();

        return this;
    }

    public NotificationUtil setImportance(int importance){
        this.importance=importance;
        return this;
    }

    public NotificationUtil setTitile(String title){
        this.title=title;
        return this;
    }

    public NotificationUtil setText(String text){
        this.text=text;
        return this;
    }

    public NotificationUtil setIconDrawable(int drawable){
        this.iconId=drawable;
        return this;
    }

    public NotificationUtil setBigIconDrawable(int drawable){
        this.bigIconId=drawable;
        return this;
    }

    public NotificationUtil addFlag(int flag){
        flagList.add(flag);
        return this;
    }

    public NotificationUtil build(RemoteViews views){
        return build(views,views);
    }

    public NotificationUtil build(RemoteViews views, RemoteViews bigViews){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        idcount++;
        String id=CHANNLE_ID+idcount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            this.notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setCustomContentView(views)
                    .setCustomBigContentView(bigViews)
                    .setSmallIcon(iconId)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setCustomContentView(views)
                    .setCustomBigContentView(bigViews)
                    .setSmallIcon(iconId)
                    .setOngoing(true);
            this.notification = notificationBuilder.build();
        }
        for(Integer f:flagList){
            notification.flags|= f;
        }
        return this;
    }

    public Notification getNotification(){
        return notification;
    }

    public int getNotificationId(){
        return idcount;
    }

    public int notifyOne(){
        return this.notifyOne(notification);
    }

    public int notifyOne(Notification notification){
        return notifyOne(idcount,notification);
    }

    public int notifyOne(int id,Notification notification){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);
        return id;
    }

    public void clearOne(int id){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void clearAll(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    private void checkEnable(){
//        if(!isNotificationEnabled(context)){
//            ToastUtil.showText(context,"无法展示通知栏信息");
//        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps =
                (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(CHECK_OP_NO_THROW,
                            Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
