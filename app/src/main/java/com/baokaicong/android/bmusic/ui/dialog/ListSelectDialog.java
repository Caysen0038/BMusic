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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.ui.view.ListRefreshFooterView;

public class ListSelectDialog {
    private ListView listView;
    private Dialog dialog;
    private Context context;
    private View root;


    private ListSelectDialog(Context context){
        this.context=context;
    }

    public static ListSelectDialog builder(Context context){
        ListSelectDialog listSelectDialog=new ListSelectDialog(context);
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
    }

    public ListSelectDialog setOnItemClickListener(AdapterView.OnItemClickListener listener){
        if(listener!=null){
            listView.setOnItemClickListener(listener);
        }
        return this;
    }

    public ListSelectDialog setListString(String[] strs){
        ListAdapter adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,strs);
        listView.setAdapter(adapter);
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
