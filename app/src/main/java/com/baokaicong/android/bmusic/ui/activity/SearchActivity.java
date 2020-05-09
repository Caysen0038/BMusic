package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.MusicSearchData;
import com.baokaicong.android.bmusic.bean.Page;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.consts.Strings;
import com.baokaicong.android.bmusic.service.MenuService;
import com.baokaicong.android.bmusic.service.MusicService;
import com.baokaicong.android.bmusic.service.UserService;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.remoter.command.InsertCommand;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.service.request.BaseRequestCallback;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.adapter.MusicListAdapter;
import com.baokaicong.android.bmusic.ui.dialog.ListSelectDialog;
import com.baokaicong.android.bmusic.ui.dialog.SheetDialog;
import com.baokaicong.android.bmusic.ui.view.ListViewPageFooter;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class SearchActivity extends AppCompatActivity {
    private ServiceConnection musicCon,menuCon;
    private MusicService musicService;
    private MenuService menuService;
    private ListView resultList;
    private ListViewPageFooter listFooter;
    private boolean searching=false;
    private MusicMenuSQLUtil musicMenuSQLUtil;
    private List<MusicMenu> menuList;
    private SearchView searchView;
    private Page page;
    private String keyword;
    private MusicListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        page=new Page();
        page.setPer(10);
        keyword="";
        init();
    }

    private void init(){
        searchView=findViewById(R.id.search_input);
        resultList=findViewById(R.id.search_result_list);
        adapter=new MusicListAdapter(this,new ArrayList<>());
        resultList.setAdapter(adapter);
        // 修改searchview 字体属性
        TextView textView =searchView.findViewById(R.id.search_src_text);
        textView.setTextColor(getResources().getColor(R.color.textWhiteAccent));
        textView.setHintTextColor(getResources().getColor(R.color.textWhiteNormal));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page.setCurrent(1);
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        musicCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService=((CustomBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicService=null;
            }
        };
        menuCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                menuService=((CustomBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                menuService=null;
            }
        };
        Intent intent=new Intent(this, MusicService.class);
        bindService(intent,musicCon,BIND_AUTO_CREATE);

        intent=new Intent(this,MenuService.class);
        bindService(intent,menuCon,BIND_AUTO_CREATE);

        musicMenuSQLUtil=new MusicMenuSQLUtil(this);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic((Music) resultList.getAdapter().getItem(position));
            }
        });
        resultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectMusic((Music) resultList.getAdapter().getItem(position));
                return true;
            }
        });

        resultList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean scrollFlag=false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        scrollFlag = true;
                        break;
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                        scrollFlag = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!searching && scrollFlag && firstVisibleItem + visibleItemCount == totalItemCount) {
                    scrollFlag=false;
                    if(page.getCurrent()<page.getTotal()){
                        page.setCurrent(page.getCurrent()+1);
                        search(keyword);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        menuList=musicMenuSQLUtil.listOwnerMenu(BMContext.instance().getUser().getUserInfo().getAccountId());
    }

    private void searchPrepare(String keyword){
        this.keyword=keyword;
        searching=true;
        searchView.clearFocus();
        if(page.getCurrent()==1 && adapter!=null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        if(listFooter==null){
            listFooter=new ListViewPageFooter(this);
            resultList.addFooterView(listFooter);
        }
        listFooter.loading();
    }

    private void search(String keyword){
        searchPrepare(keyword);
        musicService.search(BMContext.instance().getUser().getToken(), keyword,page.getCurrent(),page.getPer(),
                new RequestCallback<MusicSearchData>() {
            @Override
            public void handleResult(Result<MusicSearchData> result) {
                if(result==null)
                    return;
                page.setTotal(result.getData().getPage().getTotal());
                if(page.getCurrent()==result.getData().getPage().getCurrent()){
                    loadResult(result.getData().getMusics());
                }else{
                    page.setCurrent(result.getData().getPage().getCurrent());
                }
                searchFinish(keyword,true);
            }

            @Override
            public void handleError(Throwable t) {
                searchFinish(keyword,false);
            }
        });
    }

    private void searchFinish(String keyword,boolean success){
        searching=false;
        if(success){
            if(page.getCurrent()==page.getTotal()){
                listFooter.setText("已经滑倒底啦~~",true);
            }else{
                listFooter.setText("上滑加载更多~~",true);
            }
        }else{
            listFooter.setText("加载出错啦~~",true);
        }

    }

    private void loadResult(List<Music> list){
        adapter.addItemList(list);
        adapter.notifyDataSetChanged();
    }

    private void playMusic(Music music){
        BMContext.instance().getRemoter().command(new PlayCommand(),music);
    }

    private SheetDialog selectDialog;
    private void selectMusic(final Music music){
        selectDialog=SheetDialog.builder(this)
                .item("立即播放",android.R.drawable.ic_media_play, (v)->{ selectDialog.dismiss();playMusic(music);})
                .item("添加至歌单",android.R.drawable.ic_menu_add,(v)->{ selectDialog.dismiss();addToMenu(music);})
                .title("请选择~~")
                .create();
        selectDialog.show();
    }

    private void addToMenu(Music music){
        ListSelectDialog.ListSelectDialogBuilder builder=ListSelectDialog.builder(this);
        for(int i=0;i<menuList.size();i++){
            builder.item(menuList.get(i).getName());
        }
        final ListSelectDialog dialog= builder.title("我的歌单").create();
        dialog.setOnItemClickListener((parent,view,p,id)-> {
            MusicMenu menu = menuList.get(p);
            dialog.dismiss();
            menuService.addMusic(BMContext.instance().getUser().getToken(), menu.getMeid(), music.getMid(),
                    new BaseRequestCallback<Boolean>(SearchActivity.this) {
                        @Override
                        public void handleResult(Result<Boolean> result) {
                            if (result == null || !result.getCode().equals("000000") || !result.getData()) {
                                ToastUtil.showText(SearchActivity.this, Strings.MENU_ADD_MUSIC_FAIL);
                            } else {
                                ToastUtil.showText(SearchActivity.this, Strings.MENU_ADD_MUSIC_SUCCESS);
                                updateMenuCount(menu, 1);
                                List<Music> list=BMContext.instance().getMenuMusics(menu.getMeid());
                                if(list!=null){
                                    list.add(0,music);
                                }
                            }

                        }
                    });
        });
        dialog.show();
    }

    private void updateMenuCount(MusicMenu menu,int num){
        menu.setCount(menu.getCount()+num);
        musicMenuSQLUtil.updateMenu(menu);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputmanger.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
        if(musicService!=null){
            unbindService(musicCon);
        }
        if(menuService!=null){
            unbindService(menuCon);
        }
    }
}
