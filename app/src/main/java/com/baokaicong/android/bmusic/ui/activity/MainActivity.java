package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.MusicURL;
import com.baokaicong.android.bmusic.engine.PermissionEngine;
import com.baokaicong.android.bmusic.service.MusicPlayService;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.service.remoter.MusicRemoter;
import com.baokaicong.android.bmusic.ui.fragment.BFragment;
import com.baokaicong.android.bmusic.ui.fragment.HomeFragment;
import com.baokaicong.android.bmusic.ui.fragment.NewsFragment;
import com.baokaicong.android.bmusic.ui.view.BottomMusicView;
import com.baokaicong.android.bmusic.ui.view.MainToolbar;
import com.baokaicong.android.bmusic.util.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String NAME="main";
    private DrawerLayout drawerLayout;
    private MainToolbar toolbar;
    private static long DOUBLE_CLICK_TIME = 0L;
    private Map<Integer, BFragment> fragmentMap;
    private int lastfragment=0;
    private BottomMusicView bottomMusicView;
    private MediaRemoter mediaRemoter;
    private Map<String,Integer> navMap;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BMContext.builder().addActivity(NAME,this);
        String[] str={
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };
        PermissionEngine.Instance()
                .request(this,str);
        init();
    }

    private void init(){
        navMap=new HashMap<>();
        navMap.put("首页",R.id.fragment_home);
        navMap.put("动态",R.id.fragment_news);

        toolbar=findViewById(R.id.main_toolbar);
        toolbar.addListener(new MainToolbar.MainToolbarListener() {
            @Override
            public void onMenuClick() {
                openDrawer();
            }

            @Override
            public void onNavClick(String last, String target) {
                switchFragment(navMap.get(last),navMap.get(target));
            }
        });
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mediaRemoter=((MusicPlayService.MusicBinder)service).getRemoter();

                mediaRemoter.load(musicList());
                Log.i("主线程","获取遥控器");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("音乐服务","销毁");
            }
        };
        bottomMusicView=findViewById(R.id.bottom_music_view);
        Intent intent=new Intent(this,MusicPlayService.class);
        bindService(intent, serviceConnection,BIND_AUTO_CREATE);
        bottomMusicView.addBottomMusciListener(new BottomMusicView.BottomMusicListener() {
            @Override
            public void onPlay() {
                if(mediaRemoter!=null){
                    mediaRemoter.play();
                }

            }

            @Override
            public void onPause() {
                if(mediaRemoter!=null){
                    mediaRemoter.pause();
                }

            }

            @Override
            public void onJump(int rate) {

            }

            @Override
            public void onSwitch(String name) {
                if(mediaRemoter!=null){
                    mediaRemoter.next();
                }
            }
        });
        initFragment();
        initActionBarAndDrawer();
    }

    private List<Music> musicList(){
        List<Music> list=new ArrayList<>();
        for(String url: MusicURL.urls){
            Music m=new Music()
                    .setUrl(url);
            list.add(m);
        }
        return list;
    }


    /**
     * 初始化Fragment
     */
    private void initFragment() {

        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.fragment_home, new HomeFragment());
        fragmentMap.put(R.id.fragment_news, new NewsFragment());
        lastfragment = R.id.fragment_home;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragmentMap.get(lastfragment))
                .show(fragmentMap.get(R.id.fragment_home)).commit();
    }

    /**
     * 初始化标题栏和侧滑面板
     */
    private void initActionBarAndDrawer() {
        drawerLayout = findViewById(R.id.main_drawer);
        //创建返回键，并实现打开关/闭监听
        ActionBarDrawerToggle barDrawerToggle = new ActionBarDrawerToggerAdapter(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.syncState();
        drawerLayout.addDrawerListener(barDrawerToggle);
    }


    /**
     * 切换Fragment
     *
     * @param last
     * @param target
     */
    private void switchFragment(int last, int target) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragmentMap.get(last));//隐藏上个Fragment
        if (fragmentMap.get(target).isAdded() == false) {
            transaction.add(R.id.main_container, fragmentMap.get(target));
        }
        fragmentMap.get(target).resume();
        transaction.show(fragmentMap.get(target)).commitAllowingStateLoss();
    }

    /**
     * 响应按键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(fragmentMap.get(lastfragment).keyDown(keyCode,event)){
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && drawerLayout != null) {
            if (!closeDrawer()) {
                openDrawer();
            }
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!closeDrawer()) {
                // 设置两秒内连续两次back则退出
                if ((System.currentTimeMillis() - DOUBLE_CLICK_TIME) > 2000) {
                    ToastHelper.showText(MainActivity.this, "再按一次退出");
                    DOUBLE_CLICK_TIME = System.currentTimeMillis();
                } else {
//                    BMContext.builder().removeActivity(NAME);
//                    BMContext.builder().finishAllActivity();
//                    finish();
                    moveTaskToBack(true);
                    return true;
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 标题栏拖拽响应
     */
    private class ActionBarDrawerToggerAdapter extends ActionBarDrawerToggle {

        public ActionBarDrawerToggerAdapter(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        public ActionBarDrawerToggerAdapter(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }
    }

    /**
     * 打开侧滑面板
     * @return
     */
    private boolean openDrawer(){
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return false;
    }

    /**
     * 关闭侧滑面板
     * @return
     */
    private boolean closeDrawer(){
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        }
        return false;
    }

    // 服务是否运行
    public boolean isServiceRunning(String name) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();


        for (ActivityManager.RunningAppProcessInfo info : lists) {// 获取运行服务再启动
            System.out.println(info.processName);
            if (info.processName.equals(name)) {
                isRunning = true;
            }
        }
        return isRunning;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("主线程","销毁");
    }
}
