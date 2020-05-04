package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import com.baokaicong.android.bmusic.bean.Result;
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
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.MusicMenuSQLUtil;
import com.baokaicong.android.cdialog.consts.SheetItemColor;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BActionSheetDialog;
import com.baokaicong.android.cdialog.widget.dialog.bDialog.BMultiListViewDialog;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText inputEdit;
    private ImageButton searchButton;
    private ServiceConnection musicCon,menuCon;
    private MusicService musicService;
    private MenuService menuService;
    private ListView resultList;
    private LinearLayout loadingView;
    private boolean searching=false;
    private MusicMenuSQLUtil musicMenuSQLUtil;
    private List<MusicMenu> menuList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init(){
        inputEdit=findViewById(R.id.search_input);
        searchButton=findViewById(R.id.search_button);
        resultList=findViewById(R.id.search_result_list);
        loadingView=findViewById(R.id.loading_view);
        inputEdit.setFocusable(true);
        inputEdit.setFocusableInTouchMode(true);
        inputEdit.requestFocus();
        searchButton.setOnClickListener((v)->{
            InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
            String keyword=inputEdit.getText().toString();
            search(keyword);
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        musicCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService=((CustomBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        menuCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                menuService=((CustomBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        menuList=musicMenuSQLUtil.listOwnerMenu(BMContext.instance().getUser().getUserInfo().getAccountId());
    }

    private void search(String keyword){
        searching=true;
        switchView();
        musicService.search(BMContext.instance().getUser().getToken(), keyword,
                new RequestCallback<List<Music>>() {
            @Override
            public void handleResult(Result<List<Music>> result) {
                searching=false;
                switchView();
                if(result==null)
                    return;
                loadResult(result.getData());
            }

            @Override
            public void handleError(Throwable t) {
                searching=false;
                switchView();
                ToastUtil.showText(SearchActivity.this,"网络貌似不通畅了~~");
            }
        });
    }

    private void loadResult(List<Music> list){
        Music[] musics=new Music[list.size()];
        musics=list.toArray(musics);
        ListAdapter adapter=new MusicListAdapter(this,musics);
        resultList.setAdapter(adapter);
    }

    private void playMusic(Music music){
        BMContext.instance().getRemoter().command(new PlayCommand(),music);
    }

    private void selectMusic(final Music music){
        BActionSheetDialog dialog = new BActionSheetDialog(this).builder().setTitle("请选择");
        dialog.addSheetItem("立即播放", null, (w)-> { playMusic(music); })
                .addSheetItem("添加列表", null, (w)->{addToMenu(music);});
        dialog.show();
    }

    private void addToMenu(Music music){

        String[] strs=new String[menuList.size()];
        for(int i=0;i<strs.length;i++){
            strs[i]=menuList.get(i).getName();
        }
        ListSelectDialog dialog=ListSelectDialog.builder(this);
                dialog.setListString(strs)
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MusicMenu menu=menuList.get(position);
                        menuService.addMusic(BMContext.instance().getUser().getToken(), menu.getMeid(), music.getMid(),
                                new BaseRequestCallback<Boolean>(SearchActivity.this) {
                            @Override
                            public void handleResult(Result<Boolean> result) {
                                if(result==null || !result.getCode().equals("000000")){
                                    ToastUtil.showText(SearchActivity.this,"添加失败");
                                }else{
                                    ToastUtil.showText(SearchActivity.this,"添加成功");
                                    updateMenuCount(menu,1);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                }).show();
    }

    private void updateMenuCount(MusicMenu menu,int num){
        menu.setCount(menu.getCount()+num);
        musicMenuSQLUtil.updateMenu(menu);
    }

    private void switchView(){
        if(searching){
            resultList.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
        }else{
            resultList.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager inputmanger = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
        if(musicService!=null){
            unbindService(musicCon);
        }
        if(menuService!=null){
            unbindService(menuCon);
        }
    }
}
