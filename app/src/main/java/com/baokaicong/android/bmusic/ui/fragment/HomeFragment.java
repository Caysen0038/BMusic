package com.baokaicong.android.bmusic.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.consts.Strings;
import com.baokaicong.android.bmusic.service.MenuService;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.activity.MenuMusicsActivity;
import com.baokaicong.android.bmusic.ui.adapter.MenuListAdapter;
import com.baokaicong.android.bmusic.ui.view.IconButton;
import com.baokaicong.android.bmusic.util.sql.DownloadSQLUtil;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;
import com.baokaicong.android.cdialog.consts.SheetItemColor;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BActionSheetDialog;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BAlertDialog;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BAlertInputDialog;
import com.google.gson.Gson;

import android.content.Intent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BFragment {
    private View root;
    private List<MusicMenu> musicMenuList;
    private ListView menuList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private MusicMenuSQLUtil musicMenuSQLUtil;
    private DownloadSQLUtil downloadSQLUtil;
    private PropertySQLUtil propertySQLUtil;
    private ServiceConnection musicMenuCon;
    private MenuService menuService;
    private MenuListAdapter menuAdapter;
    private TextView usernameText;
    private TextView userDescText;
    private ImageView userImage;
    private ImageButton addMenuButton;
    private IconButton syncMenuButton;
    private TextView musicDownloadCount;
    private ObjectAnimator refreshAnimator;
    private boolean animationStopping=false;
    public HomeFragment() {
        handler=new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicMenuSQLUtil =new MusicMenuSQLUtil(getContext());
        downloadSQLUtil=new DownloadSQLUtil(getContext());
        propertySQLUtil=new PropertySQLUtil(getContext());
        musicMenuList=new ArrayList<>();

        musicMenuCon=new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                menuService =((CustomBinder)service).getService();
                syncMenu();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent=new Intent(getContext(), MenuService.class);
        getContext().bindService(intent,musicMenuCon, Context.BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.fragment_home, container, false);
        usernameText=root.findViewById(R.id.user_name);
        userDescText=root.findViewById(R.id.user_desc);
        userImage=root.findViewById(R.id.user_img);
        musicDownloadCount=root.findViewById(R.id.music_download_count);
        initSwipeRefreshLayout();
        initMusicMenuView();

        return root;
    }

    /**
     * 初始化刷新容器
     */
    private void initSwipeRefreshLayout(){
        swipeRefreshLayout=root.findViewById(R.id.fragment_home);
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

    /**
     * 初始化歌单列表
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initMusicMenuView(){
        addMenuButton=root.findViewById(R.id.add_menu_button);
        addMenuButton.setOnClickListener((v)->{createMenu();});
        syncMenuButton=root.findViewById(R.id.sync_menu_button);
        syncMenuButton.setOnClickListener((v)->{syncMenu();});
        menuList=root.findViewById(R.id.music_menu_list);
        this.menuAdapter=new MenuListAdapter(getContext(), this.musicMenuList);
        menuList.setAdapter(menuAdapter);
        menuList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean enable = true;
                // listview滑动时判断是否刷新
                if(menuList != null && menuList.getChildCount() > 0){
                    boolean firstItemVisible = menuList.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = menuList.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuItemClick(position);
            }
        });
        menuList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menuItemLongClick(position);
                return true;
            }
        });

        refreshAnimator=ObjectAnimator.ofFloat(syncMenuButton.findViewById(R.id.icon),"rotation",0,360);
        refreshAnimator.setDuration(1500);//设定转一圈的时间
        refreshAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        refreshAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        refreshAnimator.setInterpolator(new LinearInterpolator());
        // 添加监听器监听动画重复事件，保证动画优雅结束
        refreshAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animationStopping=false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 保证动画优雅的结束，每次结束动画都会回到原点
                if(animationStopping){
                    animation.cancel();
                }
            }
        });
    }


    @Override
    public void resume() {
        usernameText.setText(BMContext.instance().getUser().getUserInfo().getName());
        refreshContent();
    }

    @Override
    public boolean keyDown(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * 刷新内容
     */
    private void refreshContent(){
        List<MusicMenu> list=musicMenuSQLUtil.listOwnerMenu(BMContext.instance().getUser().getUserInfo().getAccountId());
        loadMusicMenu(list);
        musicDownloadCount.setText(downloadSQLUtil.countAll()+"首");
        refreshFinished();
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

    private void syncPrepare(){

        refreshAnimator.start();
    }
    private void syncComplete(){
        // 不调用stop，让监听器优雅结束
        animationStopping=true;
    }
    private boolean checkMusicsSyncState(){
        for(MusicMenu menu:musicMenuList){
            if(BMContext.instance().getMenuMusics(menu.getMeid())==null){
                return false;
            }
        }
        return true;
    }
    /**
     * 同步歌单
     */
    private void syncMenu(){
        syncPrepare();
        menuService.syncMenus(BMContext.instance().getUser().getToken(), new RequestCallback<List<MusicMenu>>() {
            @Override
            public void handleResult(Result<List<MusicMenu>> result) {
//                if(result==null){
//                    ToastUtil.showText(getContext(),Strings.DATA_LOAD_ERROR);
//                    return;
//                }
//                switch (result.getCode()){
//                    case "000000":
                        refreshContent();
                        syncMenuMusics();
//                        break;
//                    case "100000":
//                        ToastUtil.showText(getContext(),Strings.DATA_LOAD_ERROR);
//                        return;
//                }

            }

            @Override
            public void handleError(Throwable t) {
                ToastUtil.showText(getContext(), Strings.NET_ERROR);
                syncComplete();
            }
        });
    }

    private void syncMenuMusics(){
        syncPrepare();
        for(MusicMenu menu:musicMenuList){
            menuService.getMenuMusics(BMContext.instance().getUser().getToken(), menu.getMeid(),
                    new RequestCallback<List<Music>>() {
                        @Override
                        public void handleResult(Result<List<Music>> result) {
//                            if(result==null)
//                                return;
                            List<Music> list=result.getData();
//                            if(list!=null){
                            BMContext.instance().addMenuMusics(menu.getMeid(),list);
//                            }
                            if(checkMusicsSyncState()){
                                syncComplete();
                            }
                        }

                        @Override
                        public void handleError(Throwable t) {
                            ToastUtil.showText(getContext(),Strings.NET_ERROR);
                            BMContext.instance().addMenuMusics(menu.getMeid(),new ArrayList<>());
                            if(checkMusicsSyncState()){
                                syncComplete();
                            }
                        }
                    });
        }
    }

    private void loadMusicMenu(List<MusicMenu> list){
        musicMenuList.clear();
        for(MusicMenu m:list){
            this.musicMenuList.add(m);
        }
        this.menuAdapter.notifyDataSetChanged();
    }

    /**
     * 歌单点击
     * @param p
     */
    private void menuItemClick(int p){
        MusicMenu menu= (MusicMenu) menuAdapter.getItem(p);
        if(BMContext.instance().getMenuMusics(menu.getMeid())!=null){
            Intent intent=new Intent(getContext(), MenuMusicsActivity.class);
            intent.putExtra("meid",menu.getMeid());
            startActivity(intent);
        }else{
            ToastUtil.showText(getContext(),Strings.MENU_LOADING);
        }

    }

    /**
     * 歌单长按
     * @param p
     */
    private void menuItemLongClick(int p){
        MusicMenu menu= (MusicMenu) menuAdapter.getItem(p);
        openMusicMenuDialog(menu);
    }

    private void openMusicMenuDialog(MusicMenu menu){
        BActionSheetDialog dialog = new BActionSheetDialog(getContext()).builder().setTitle("请选择");
        dialog.addSheetItem("播放列表", null, (w)-> { playMenu(menu); })
                .addSheetItem("下载", null,(w)-> { downloadMenu(menu);})
                .addSheetItem("重命名", null, (w)->{renameMenu(menu);})
                .addSheetItem("删除", SheetItemColor.Red,(w)-> { deleteMenu(menu);});
        dialog.show();
    }

    private void renameMenu(MusicMenu menu){
        final BAlertInputDialog inputDialog= new BAlertInputDialog(getContext()).builder()
                .setTitle("歌单名称")
                .setEditHint("请输入歌单名");
        inputDialog.setNegativeButton("取消", (v)-> { inputDialog.dismiss(); })
                .setPositiveButton("确认", (v)-> {
                    inputDialog.dismiss();
                    menuService.renameMenu(BMContext.instance().getUser().getToken(),
                            menu.getMeid(), inputDialog.getResult(), new RequestCallback<Boolean>() {
                                @Override
                                public void handleResult(Result<Boolean> result) {
                                    refreshContent();
                                }

                                @Override
                                public void handleError(Throwable t) {
                                    ToastUtil.showText(getContext(),"网络貌似不通畅~~");
                                }
                            });
                });
        inputDialog.show();
    }

    private void deleteMenu(MusicMenu menu){
        BAlertDialog myAlertDialog = new BAlertDialog(getContext()).builder()
                .setTitle("确认删除该歌单吗？")
                .setMsg("删除"+menu.getName())
                .setPositiveButton("确认", (v)-> {
                    menuService.dropMenu(BMContext.instance().getUser().getToken(),
                            menu.getMeid(), new RequestCallback<Boolean>() {
                                @Override
                                public void handleResult(Result<Boolean> result) {
                                    refreshContent();
                                }

                                @Override
                                public void handleError(Throwable t) {
                                    ToastUtil.showText(getContext(),"网络貌似不通畅~~");
                                }
                            });
                })
                .setNegativeButton("取消",(v) ->{ return; });
        myAlertDialog.show();
    }

    private void createMenu(){
        final BAlertInputDialog inputDialog= new BAlertInputDialog(getContext()).builder()
                .setTitle("歌单名称")
                .setEditHint("请输入歌单名");

        inputDialog.setNegativeButton("取消", (v)-> { inputDialog.dismiss(); })
                .setPositiveButton("确认", (v)-> {
                    inputDialog.dismiss();
                    menuService.createMenu(BMContext.instance().getUser().getToken(),
                            inputDialog.getResult(), new RequestCallback<Boolean>() {
                                @Override
                                public void handleResult(Result<Boolean> result) {
                                    syncMenu();
                                }

                                @Override
                                public void handleError(Throwable t) {
                                    ToastUtil.showText(getContext(),"网络貌似不通畅~~");
                                }
                            });
                });
        inputDialog.show();
    }

    private void playMenu(MusicMenu menu){

    }

    private void downloadMenu(MusicMenu menu){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(menuService !=null){
            getContext().unbindService(musicMenuCon);
        }
        if(musicMenuSQLUtil!=null){
            musicMenuSQLUtil.close();
        }
        if(downloadSQLUtil!=null){
            downloadSQLUtil.close();
        }
        if(propertySQLUtil!=null){
            propertySQLUtil.close();
        }
    }
}
