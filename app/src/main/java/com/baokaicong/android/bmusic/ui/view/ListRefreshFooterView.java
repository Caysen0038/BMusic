package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

public class ListRefreshFooterView extends LinearLayout {

    public ListRefreshFooterView(Context context) {
        super(context);
        initView();
    }

    public ListRefreshFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ListRefreshFooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_refresh_footer,this,true);
    }
}
