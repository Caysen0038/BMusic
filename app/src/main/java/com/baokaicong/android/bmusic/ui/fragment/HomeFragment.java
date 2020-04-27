package com.baokaicong.android.bmusic.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.MusicMenuService;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.adapter.MusicMenuListAdapter;
import com.baokaicong.android.bmusic.ui.view.IconButton;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.cdialog.consts.SheetItemColor;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BActionSheetDialog;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BAlertInputDialog;

import android.content.Intent;
import android.widget.TextView;

import java.util.List;


public class HomeFragment extends BFragment {
    private View root;
    private ListView menuList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private MusicMenuSQLUtil musicMenuSQLUtil;
    private ServiceConnection musicMenuCon;
    private MusicMenuService musicMenuService;
    private MusicMenuListAdapter menuAdapter;
    private TextView usernameText;
    private TextView userDescText;
    private ImageView userImage;
    private ImageButton addMenuButton;
    private IconButton syncMenuButton;
    public HomeFragment() {
        handler=new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicMenuSQLUtil =new MusicMenuSQLUtil(getContext());
        musicMenuCon=new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicMenuService=((CustomBinder)service).getService();
                refreshContent();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent=new Intent(getContext(),MusicMenuService.class);
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
        usernameText.setText(BMContext.instance().getUser().getUserInfo().getName());
        initSwipeRefreshLayout();
        initMusicMenuView();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

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
    }


    @Override
    public void resume() {

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

    /**
     * 同步歌单
     */
    private void syncMenu(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(syncMenuButton.findViewById(R.id.icon),"rotation",0,360*3);
        animator.setDuration(1400);
        animator.start();
        musicMenuService.syncMenus(BMContext.instance().getUser().getToken(), new RequestCallback<List<MusicMenu>>() {
            @Override
            public void handleResult(Result<List<MusicMenu>> result) {
                //
                if(result==null){
                    ToastUtil.showText(getContext(),"歌单数据错误");
                    return;
                }
                switch (result.getCode()){
                    case "000000":
                        refreshContent();
                        break;
                    case "100000":
                        ToastUtil.showText(getContext(),"歌单数据错误");
                        return;
                }
                //animator.end();
            }

            @Override
            public void handleError(Throwable t) {
                ToastUtil.showText(getContext(),"歌单请求错误");
                //animator.end();
            }
        });
    }



    private void loadMusicMenu(List<MusicMenu> list){
        MusicMenu[] menu=new MusicMenu[list.size()];
        int i=0;
        for(MusicMenu m:list){
            menu[i++]=m;
        }
        this.menuAdapter=new MusicMenuListAdapter(getContext(), menu);
        menuList.setAdapter(menuAdapter);
    }

    /**
     * 歌单点击
     * @param p
     */
    private void menuItemClick(int p){

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

    }

    private void deleteMenu(MusicMenu menu){

    }

    private void createMenu(){
        final BAlertInputDialog inputDialog= new BAlertInputDialog(getContext()).builder()
                .setTitle("歌单名称")
                .setEditHint("请输入歌单名");

        inputDialog.setNegativeButton("取消", (v)-> { inputDialog.dismiss(); })
                .setPositiveButton("确认", (v)-> {
                    inputDialog.dismiss();
                    ToastUtil.showText(getContext(),inputDialog.getResult());
                });
        inputDialog.show();
    }

    private void playMenu(MusicMenu menu){

    }

    private void downloadMenu(MusicMenu menu){

    }

}
