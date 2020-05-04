package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.consts.SettingField;
import com.baokaicong.android.bmusic.service.UserService;
import com.baokaicong.android.bmusic.service.remoter.command.NextCommand;
import com.baokaicong.android.bmusic.service.remoter.command.OpenCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PauseCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.service.remoter.command.ShutdownCommand;
import com.baokaicong.android.bmusic.util.BEAUtil;
import com.baokaicong.android.bmusic.util.PermissionUtil;
import com.baokaicong.android.bmusic.service.MusicPlayService;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.ui.fragment.BFragment;
import com.baokaicong.android.bmusic.ui.fragment.HomeFragment;
import com.baokaicong.android.bmusic.ui.fragment.NewsFragment;
import com.baokaicong.android.bmusic.ui.view.BottomMusicView;
import com.baokaicong.android.bmusic.ui.view.MainToolbar;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String NAME="main";
    private DrawerLayout drawerLayout;
    private MainToolbar toolbar;
    private static long DOUBLE_CLICK_TIME = 0L;
    private Map<Integer, BFragment> fragmentMap;
    private int currentFragment=0;
    private BottomMusicView bottomMusicView;
    private MediaRemoter mediaRemoter;
    private Map<String,Integer> navMap;
    private ServiceConnection musicPlayCon;
    private ServiceConnection userCon;
    private UserService userService;
    private Button logoutButton;
    private PropertySQLUtil propertySQLUtil;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BMContext.instance().addActivity(NAME,this);
        String[] str={
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };
        PermissionUtil.Instance()
                .request(this,str);
        propertySQLUtil =new PropertySQLUtil(this);
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

        logoutButton=findViewById(R.id.drawer_quit_button);
        logoutButton.setOnClickListener((v)->{logout();});

        searchButton=findViewById(R.id.search);
        searchButton.setOnClickListener((v)->{
            Intent intent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
        });
        // 播放服务
        musicPlayCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mediaRemoter=((MusicPlayService.MusicBinder)service).getRemoter();
                mediaRemoter.command(new OpenCommand(),null);
                BMContext.instance().setRemoter(mediaRemoter);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("音乐服务","销毁");
            }
        };
        Intent intent=new Intent(this,MusicPlayService.class);
        bindService(intent, musicPlayCon,BIND_AUTO_CREATE);

        bottomMusicView=findViewById(R.id.bottom_music_view);
        bottomMusicView.addBottomMusciListener(new BottomMusicView.BottomMusicListener() {
            @Override
            public void onPlay() {
                if(mediaRemoter!=null){
                    mediaRemoter.command(new PlayCommand(),null);
                }
            }

            @Override
            public void onPause() {
                if(mediaRemoter!=null){
                    mediaRemoter.command(new PauseCommand(),null);
                }

            }

            @Override
            public void onJump(int rate) {

            }

            @Override
            public void onSwitch(Music music) {
                if(mediaRemoter!=null){
                    mediaRemoter.command(new NextCommand(),null);
                }
            }
        });
        initFragment();
        initActionBarAndDrawer();
        initDrawerView();
    }

    private void initDrawerView(){
        String[] names={"我的信息","设置","网络"};
        ListView list=findViewById(R.id.drawer_nav_list);
        ListAdapter adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,names);
        list.setAdapter(adapter);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.fragment_home, new HomeFragment());
        fragmentMap.put(R.id.fragment_news, new NewsFragment());
        currentFragment = R.id.fragment_home;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragmentMap.get(currentFragment))
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

    @Override
    protected void onResume() {
        super.onResume();
        for(BFragment f:fragmentMap.values()){
            f.resume();
        }
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
        if(fragmentMap.get(currentFragment).keyDown(keyCode,event)){
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
                    DOUBLE_CLICK_TIME = System.currentTimeMillis();
                } else {
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

    /**
     * 注销登录
     */
    private void logout(){
        String username= propertySQLUtil.getProperty(SettingField.USER_NAME);
        String password= propertySQLUtil.getProperty(SettingField.USER_PASSWORD);
        propertySQLUtil.deleteProperty(SettingField.USER_NAME);
        propertySQLUtil.deleteProperty(SettingField.USER_PASSWORD);
        BMContext.instance().setCurrentUser(null);
        BMContext.instance().getRemoter().command(new ShutdownCommand(),null);
        Intent intent=new Intent(this,LoginActivity.class);
        intent.putExtra(SettingField.USER_NAME,username);
        if(password!=null && password.length()>0){
            password= BEAUtil.DBEA(password,username);
        }
        intent.putExtra(SettingField.USER_PASSWORD,password);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(musicPlayCon!=null){
            unbindService(musicPlayCon);
        }
        if(userCon!=null){
            unbindService(userCon);
        }
        if(propertySQLUtil !=null){
            propertySQLUtil.close();
        }
        mediaRemoter.command(new ShutdownCommand(),null);
        mediaRemoter=null;
    }
}
