package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

import pl.droidsonroids.gif.GifImageView;

public class ListViewPageFooter extends LinearLayout {
    private Context context;
    private GifImageView loadGif;
    private TextView textView;

    public ListViewPageFooter(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public ListViewPageFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public ListViewPageFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    private void initView(){
        LayoutInflater.from(context).inflate(R.layout.view_list_page_footer,this,true);
        loadGif=findViewById(R.id.footer_gif);
        textView=findViewById(R.id.footer_text);
    }

    public void loading(){
        loadGif.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    public void loadEnd(){
        loadGif.setVisibility(GONE);
    }

    public void showText(){
        loadGif.setVisibility(GONE);
        textView.setVisibility(VISIBLE);
    }

    public void setText(String text){
        textView.setText(text);
    }

    public void setText(String text,boolean show){
        setText(text);
        if(show){
            showText();
        }
    }



}
