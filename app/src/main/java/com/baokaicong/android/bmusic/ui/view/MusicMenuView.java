package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

public class MusicMenuView extends LinearLayout {
    private Context context;
    private ImageView icon;
    private TextView name;
    public MusicMenuView(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public MusicMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public MusicMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    private void initView(){
        LayoutInflater.from(context).inflate(R.layout.item_home_menu,this,true);
        name=findViewById(R.id.music_menu_name);
        icon=findViewById(R.id.music_menu_icon);
    }

    public MusicMenuView setIcon(Drawable drawable){
        this.icon.setImageDrawable(drawable);
        return this;
    }

    public MusicMenuView setName(String str){
        this.name.setText(str);
        return this;
    }
}
