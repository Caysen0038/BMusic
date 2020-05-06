package com.baokaicong.android.bmusic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.ui.view.dialog.SheetItem;

import java.util.ArrayList;
import java.util.List;

public class SheetDialog {
    private LinearLayout itemContainer;
    private Dialog dialog;
    private Context context;
    private View root;
    private SheetItem cancelItem;
    private TextView titleView;


    public SheetDialog(Context context){
        this.context=context;
    }

    public static SheetDialogBuilder builder(Context context){
        return new SheetDialogBuilder(context);
    }

    public static class SheetDialogBuilder{
        private Context context;
        private List<SheetItem> list;
        private String title="请选择";
        private SheetDialogBuilder(Context context){
            this.context=context;
            list=new ArrayList<>();
        }

        public SheetDialogBuilder item(String text, View.OnClickListener listener){
            return item(text,-1,-1,listener);
        }
        public SheetDialogBuilder item(String text, int icon, View.OnClickListener listener){
            return item(text,icon,-1,listener);
        }
        public SheetDialogBuilder item(String text, int icon, int textColor, View.OnClickListener listener){
            SheetItem item=new SheetItem(context);
            item.setText(text);
            if(icon!=-1){
                item.setIcon(icon);
            }
            if(textColor!=-1){
                item.setTextColor(textColor);
            }
            if(listener!=null){
                item.setOnClickListener(listener);
            }
            list.add(item);
            return this;
        }

        public SheetDialogBuilder title(String title){
            this.title=title;
            return this;
        }

        public SheetDialog create(){
            SheetDialog dialog=new SheetDialog(context);
            dialog.initView();
            if(list.size()>0){
                dialog.loadItem(list);
            }
            dialog.setTitle(title);
            return dialog;
        }

    }

    private void initView(){
        root = LayoutInflater.from(context).inflate(R.layout.dialog_sheet_select, null);
        itemContainer=root.findViewById(R.id.item_container);
        cancelItem=root.findViewById(R.id.item_cancel);
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

        cancelItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        cancelItem.setOnClickListener((v)->{
            dismiss();
        });
    }


    public SheetDialog loadItem(List<SheetItem> list){
        for(SheetItem item:list){
            itemContainer.addView(item);
        }
        return this;
    }

    public SheetDialog setTitle(String title){
        this.titleView.setText(title);
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
