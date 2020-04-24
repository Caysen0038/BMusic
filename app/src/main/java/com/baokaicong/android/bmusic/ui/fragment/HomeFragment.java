package com.baokaicong.android.bmusic.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.ui.adapter.MusicMenuListAdapter;


public class HomeFragment extends BFragment {
    private View root;
    private ListView menuList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    public HomeFragment() {
        handler=new Handler();
    }


    public static HomeFragment create() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.fragment_home, container, false);
        ListAdapter adapter=new MusicMenuListAdapter(getContext(),menus());
        menuList=root.findViewById(R.id.music_menu_list);
        menuList.setAdapter(adapter);
        swipeRefreshLayout=root.findViewById(R.id.fragment_home);
        initSwipeRefreshLayout();
        initMusicMenuView();
        return root;
    }

    /**
     * 初始化刷新容器
     */
    private void initSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryLight,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshContent();
                refreshFinished();
            }
        });

    }

    /**
     * 初始化歌单列表
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initMusicMenuView(){
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
                //fileItemClick(position);
            }
        });
        menuList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //fileItemLongClick(position);
                return true;
            }
        });
    }

    private MusicMenu[] menus(){
        MusicMenu[] menus=new MusicMenu[10];
        for(int i=0;i<menus.length;i++){
            menus[i]=new MusicMenu();
            menus[i].setName(i+"");
        }
        return menus;
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

    @Override
    public void resume() {

    }

    @Override
    public boolean keyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
