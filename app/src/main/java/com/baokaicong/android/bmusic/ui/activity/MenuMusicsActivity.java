package com.baokaicong.android.bmusic.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicList;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.service.MenuService;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.remoter.MediaRemoter;
import com.baokaicong.android.bmusic.service.remoter.command.LoadListCommand;
import com.baokaicong.android.bmusic.service.remoter.command.NextCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PauseCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.adapter.MusicListAdapter;
import com.baokaicong.android.bmusic.ui.view.BottomMusicView;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.cdialog.consts.SheetItemColor;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BActionSheetDialog;
import com.google.gson.Gson;

import java.util.List;

public class MenuMusicsActivity extends AppCompatActivity {
    private MusicMenu menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private ImageView menuImg;
    private MusicList musicList;
    private TextView menuName;
    private TextView menuOnwer;
    private ListView musicListView;
    private ServiceConnection musicMenuCon;
    private MenuService menuService;
    private MediaRemoter remoter;
    private BottomMusicView bottomMusicView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_musics);
        handler=new Handler();
        String m=getIntent().getStringExtra("menu");
        if(m!=null){
            menu=new Gson().fromJson(m,MusicMenu.class);
        }
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init(){
        remoter=BMContext.instance().getRemoter();
        menuImg=findViewById(R.id.menu_img);
        menuName=findViewById(R.id.menu_name);
        menuOnwer=findViewById(R.id.menu_owner);
        if(menu!=null){
            menuName.setText(menu.getName());
            menuOnwer.setText("by "+ BMContext.instance().getUser().getUserInfo().getName());
        }
        musicMenuCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                menuService =((CustomBinder)service).getService();
                refreshContent();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bottomMusicView=findViewById(R.id.bottom_music_view);
        bottomMusicView.addBottomMusciListener(new BottomMusicView.BottomMusicListener() {
            @Override
            public void onPlay() {
                remoter.command(new PlayCommand(),null);
            }

            @Override
            public void onPause() {
                remoter.command(new PauseCommand(),null);
            }

            @Override
            public void onJump(int rate) {

            }

            @Override
            public void onSwitch(Music music) {
                remoter.command(new NextCommand(),null);
            }
        });
        Intent intent=new Intent(this, MenuService.class);
        this.bindService(intent,musicMenuCon,BIND_AUTO_CREATE);
        initSwipeRefreshLayout();
        initMusicListView();
    }

    private void initSwipeRefreshLayout(){
        swipeRefreshLayout=findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryLight,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initMusicListView(){
        musicListView=findViewById(R.id.music_list);
        musicListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean enable = true;
                // listview滑动时判断是否刷新
                if(musicListView != null && musicListView.getChildCount() > 0){
                    boolean firstItemVisible = musicListView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = musicListView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadAndPlay((Music) musicListView.getAdapter().getItem(position));
            }
        });

        musicListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectMusic((Music) musicListView.getAdapter().getItem(position));
                return true;
            }
        });
    }

    /**
     * 刷新内容
     */
    private void refreshContent(){
        if(menuService ==null){
            return;
        }
        menuService.getMenuMusics(BMContext.instance().getUser().getToken(), menu.getMeid(),
                new RequestCallback<List<Music>>() {
                    @Override
                    public void handleResult(Result<List<Music>> result) {
                        if(result==null)
                            return;
                        List<Music> list=result.getData();
                        if(list!=null && list.size()>0){
                            loadMusicList(list);
                        }
                        refreshFinished();
                    }

                    @Override
                    public void handleError(Throwable t) {
                        ToastUtil.showText(MenuMusicsActivity.this,"加载歌曲列表出错");
                        refreshFinished();
                    }
                });
    }

    /**
     * 内容刷新结束
     */
    private void refreshFinished(){
        if (swipeRefreshLayout.isRefreshing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void loadMusicList(List<Music> list){
        Music[] musics=new Music[list.size()];
        musicList=new MusicList();
        int i=0;
        for(Music m:list){
            musics[i++]=m;
            musicList.add(m);
        }

        ListAdapter adapter=new MusicListAdapter(this,musics);
        musicListView.setAdapter(adapter);
    }

    private void loadAndPlay(Music music){
        MediaRemoter remoter=BMContext.instance().getRemoter();
        remoter.command(new LoadListCommand(),musicList);
        playMusic(music);
    }

    private void playMusic(Music music){
        MediaRemoter remoter=BMContext.instance().getRemoter();
        remoter.command(new PlayCommand(),music);
        Intent intent=new Intent(this,MusicActivity.class);
        startActivity(intent);
    }

    private void selectMusic(Music music){
        BActionSheetDialog dialog = new BActionSheetDialog(this).builder().setTitle("请选择");
        dialog.addSheetItem("立即播放", null, (w)-> { playMusic(music); })
                .addSheetItem("删除", SheetItemColor.Red, (w)->{removeMusic(music);});
        dialog.show();
    }

    private void removeMusic(Music music){
        if(menuService ==null){
            return;
        }
        menuService.dropMusic(BMContext.instance().getUser().getToken(),menu.getMeid(), music.getMid(), new RequestCallback<Boolean>() {
            @Override
            public void handleResult(Result<Boolean> result) {
                if(result==null || !result.getCode().equals("000000")){
                    ToastUtil.showText(MenuMusicsActivity.this,"删除失败");
                    return;
                }
                if(result.getData()){
                    ToastUtil.showText(MenuMusicsActivity.this,music.getName()+"已移除");
                }else{
                    ToastUtil.showText(MenuMusicsActivity.this,music.getName()+"未能移除");
                }
            }

            @Override
            public void handleError(Throwable t) {
                ToastUtil.showText(MenuMusicsActivity.this,"服务器异常");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(menuService !=null){
            unbindService(musicMenuCon);
        }
    }
}
