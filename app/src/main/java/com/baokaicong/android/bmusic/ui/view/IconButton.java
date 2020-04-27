package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

public class IconButton extends LinearLayout {
    private Context context;
    private TypedArray typedArray;
    private int icon=-1;
    private ImageView img;
    public IconButton(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public IconButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        parseAttr(attrs);
        initView();
    }

    public IconButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        parseAttr(attrs);
        initView();
    }

    private void parseAttr(AttributeSet attrs){
        typedArray=context.obtainStyledAttributes(attrs, R.styleable.IconButton);
        icon=typedArray.getResourceId(R.styleable.IconButton_icon,-1);
    }

    private void initView(){
        LayoutInflater.from(context).inflate(R.layout.view_icon_button,this,true);
        img=findViewById(R.id.icon);
        if(icon!=-1){
            img.setImageResource(icon);
        }
    }

    public ImageView getIcon(){
        return this.img;
    }

}
