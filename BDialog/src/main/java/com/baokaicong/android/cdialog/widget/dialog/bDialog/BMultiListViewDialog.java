package com.baokaicong.android.cdialog.widget.dialog.bDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baokaicong.android.cdialog.R;
import com.baokaicong.android.cdialog.widget.autoloadListView.AutoLoadListView;
import com.baokaicong.android.cdialog.widget.autoloadListView.LoadingFooter;

public class BMultiListViewDialog {

    private Context context;
    private Dialog dialog;
    private Display display;
    private AutoLoadListView listView;
    private TextView cancelTv, sureTv, titleTv;

    public BMultiListViewDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public BMultiListViewDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_multi_list_view, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());
        listView = view.findViewById(R.id.list_view);
        cancelTv = view.findViewById(R.id.tv_cancel);
        sureTv = view.findViewById(R.id.tv_sure);
        titleTv = view.findViewById(R.id.tv_title);
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public BMultiListViewDialog setAdapter(BaseAdapter mAdapter) {
        listView.setAdapter(mAdapter);
        return this;
    }

    public BMultiListViewDialog setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
        return this;
    }

    public BMultiListViewDialog setNegativeButton(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            cancelTv.setText(context.getString(R.string.cancel));
        } else {
            cancelTv.setText(text);
        }
        cancelTv.setOnClickListener(listener);
        return this;
    }

    public BMultiListViewDialog setLoadNextListener(AutoLoadListView.OnLoadNextListener listener) {
        listView.setOnLoadNextListener(listener);
        return this;
    }

    public AutoLoadListView getListView() {
        return listView;
    }

    public BMultiListViewDialog setListViewEndState() {
        listView.setState(LoadingFooter.State.TheEnd);
        return this;
    }


    public BMultiListViewDialog setListViewLoading() {
        listView.setState(LoadingFooter.State.Idle);
        return this;
    }

    public BMultiListViewDialog setPostiveButton(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            sureTv.setText(context.getString(R.string.confirm));
        } else {
            sureTv.setText(text);
        }
        sureTv.setOnClickListener(listener);
        return this;
    }

    public BMultiListViewDialog setTitle(String text) {
        titleTv.setText(text);
        return this;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        } catch (Exception e) {

        }
    }
}
