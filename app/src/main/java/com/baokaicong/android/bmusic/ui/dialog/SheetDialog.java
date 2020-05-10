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

public class SheetDialog extends Dialog{
    private LinearLayout itemContainer;
    private Context context;
    private View root;
    private SheetItem cancelItem;
    private TextView titleView;


    public SheetDialog(Context context){
        super(context,R.style.ActionSheetDialogStyle);
        this.context=context;
        initView();
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
        setContentView(root);
        Window dialogWindow = getWindow();
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

    public SheetDialog addItem(String text, View.OnClickListener listener){
        return addItem(text,0,0,listener);
    }

    public SheetDialog addItem(String text, int imgId, View.OnClickListener listener){
        return addItem(text,imgId,0,listener);
    }

    public SheetDialog addItem(String text, int imgId,int textColor, View.OnClickListener listener){
        SheetItem item=new SheetItem(context);
        item.setText(text);
        if(imgId>0){
            item.setIcon(imgId);
        }
        if(textColor>0){
            item.setTextColor(textColor);
        }
        item.setOnClickListener(new DialogClickListener(listener));
        itemContainer.addView(item);
        return this;
    }

    public SheetDialog setTitle(String title){
        this.titleView.setText(title);
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private class DialogClickListener implements View.OnClickListener{
        private View.OnClickListener listener;
        public DialogClickListener(View.OnClickListener listener){
            this.listener=listener;
        }

        @Override
        public void onClick(View v) {
            dismiss();
            if(listener!=null){
                listener.onClick(v);
            }
        }
    }
}
