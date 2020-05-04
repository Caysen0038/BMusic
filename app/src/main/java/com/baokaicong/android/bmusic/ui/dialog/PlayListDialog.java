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
import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.service.remoter.command.PlayCommand;
import com.baokaicong.android.bmusic.ui.adapter.PlayListAdapter;

public class PlayListDialog {
    private ListView listView;
    private Dialog dialog;
    private Context context;
    private View root;
    private boolean autoPlay=true;


    private PlayListDialog(Context context){
        this.context=context;
    }

    public static PlayListDialog builder(Context context){
        PlayListDialog listSelectDialog=new PlayListDialog(context);
        listSelectDialog.initView();
        return listSelectDialog;
    }

    private void initView(){
        root = LayoutInflater.from(context).inflate(R.layout.dialog_list_select, null);
        listView=root.findViewById(R.id.item_list);
        
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        root.setMinimumWidth(display.getWidth());
        dialog = new Dialog(context, com.baokaicong.android.cdialog.R.style.ActionSheetDialogStyle);
        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        loadPlayList();
    }

    private void loadPlayList(){
        PlayListAdapter adapter=new PlayListAdapter(context, BMContext.instance().getPlayInfo().getMusicList().getList());
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
