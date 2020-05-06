package com.baokaicong.android.bmusic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.ui.adapter.PlayListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlayListDialog {
    private ListView listView;
    private Dialog dialog;
    private Context context;
    private View root;
    private boolean autoPlay=true;
    private TextView titleView;

    private PlayListDialog(Context context){
        this.context=context;
    }

    public static PlayListDialogBuilder builder(Context context){
        return new PlayListDialogBuilder(context);
    }

    public static class PlayListDialogBuilder{
        private Context context;
        private List<Music> musicList;
        private String title;
        private boolean auto=true;
        private AdapterView.OnItemClickListener listener;
        public PlayListDialogBuilder(Context context){
            this.context=context;
            musicList=new ArrayList<>();
        }
        public PlayListDialogBuilder list(Music music){
            this.musicList.add(music);
            return this;
        }

        public PlayListDialogBuilder title(String text){
            this.title=text;
            return this;
        }
        public PlayListDialogBuilder autoPlay(boolean auto){
            this.auto=auto;
            return this;
        }
        public PlayListDialogBuilder listener(AdapterView.OnItemClickListener listener){
            this.listener=listener;
            return this;
        }
        public PlayListDialog create(){
            PlayListDialog dialog=new PlayListDialog(context);
            dialog.initView()
                .loadPlayList(musicList)
                .setTitile(title)
                .setAutoPlay(auto);
            if(listener!=null){
                dialog.setOnItemClickListener(listener);
            }
            return dialog;
        }
    }

    private PlayListDialog initView(){
        root = LayoutInflater.from(context).inflate(R.layout.dialog_paly_list, null);
        listView=root.findViewById(R.id.item_list);
        titleView=root.findViewById(R.id.dialog_title);
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        root.setMinimumWidth(display.getWidth());
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }

    public PlayListDialog setTitile(String text){
        titleView.setText(text);
        return this;
    }

    public PlayListDialog loadPlayList(List<Music> list){
        PlayListAdapter adapter=new PlayListAdapter(context, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(autoPlay){
                    Music music=getMusic(position);
                    BMContext.instance().getRemoter().command(new PlayCommand(),music);
                    dismiss();
                }
            }
        });
        return this;
    }

    public PlayListDialog setOnItemClickListener(AdapterView.OnItemClickListener listener){
        if(listener!=null){
            listView.setOnItemClickListener(listener);
        }
        return this;
    }

    public Music getMusic(int i){
        return (Music) listView.getItemAtPosition(i);
    }

    public PlayListDialog setAutoPlay(boolean auto){
        this.autoPlay=auto;
        return this;
    }

    public void show(){
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss(){
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        } catch (Exception e) {

        }
    }


}
