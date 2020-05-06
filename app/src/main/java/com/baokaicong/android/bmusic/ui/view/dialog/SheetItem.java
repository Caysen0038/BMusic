package com.baokaicong.android.bmusic.ui.view.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.R;

public class SheetItem extends LinearLayout {
    private TypedArray typedArray;
    private ImageView iconView;
    private TextView textView;

    public SheetItem(Context context) {
        super(context);
        initView();
    }

    public SheetItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public SheetItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(){
       initView(null);
    }

    private void initView(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.dialog_view_sheet_item,this,true);
        iconView=findViewById(R.id.item_icon);
        textView=findViewById(R.id.item_text);
        if(attrs!=null){
            parseAttr(attrs);
        }
    }

    private void parseAttr(AttributeSet attrs){
        typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.SheetItem);
        int resid=typedArray.getResourceId(R.styleable.SheetItem_itemIcon,-1);
        if(resid!=-1){
            setIcon(resid);
        }
        int color=typedArray.getColor(R.styleable.SheetItem_itemTextColor,getResources().getColor(R.color.textWhiteNormal));
        setTextColor(color);
        String text=typedArray.getString(R.styleable.SheetItem_itemText);
        if(text==null){
            text="";
        }
        setText(text);
    }



    public SheetItem setText(String text){
        textView.setText(text);
        return this;
    }

    public SheetItem setTextColor(int color){
        textView.setTextColor(color);
        return this;
    }

    public SheetItem setIcon(int resid){
        iconView.setImageResource(resid);
        return this;
    }
}
