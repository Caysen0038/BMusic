package com.baokaicong.android.bmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.baokaicong.android.bmusic.R;

import java.util.ArrayList;
import java.util.List;


public class MainToolbar extends LinearLayout {
    private Context context;
    private ImageButton menuButton;
    private Button[] buttons;
    private List<MainToolbarListener> listenerList;
    public interface MainToolbarListener{
        void onMenuClick();
        void onNavClick(String last,String target);
    }

    public MainToolbar(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public MainToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public MainToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    private void initView(){
        listenerList=new ArrayList<>();
        LayoutInflater.from(context).inflate(R.layout.view_main_toolbar,this,true);
        buttons=new Button[2];
        buttons[0]=findViewById(R.id.toolbar_home);
        buttons[1]=findViewById(R.id.toolbar_news);
        OnTextButtonClickListener clickListener=new OnTextButtonClickListener(buttons[0]);
        for(Button b:buttons){
            b.setOnClickListener(clickListener);
        }

        menuButton=findViewById(R.id.toolbar_menu);
        menuButton.setOnClickListener((v)->{
            notifyMenuClick();
        });
    }


    private class OnTextButtonClickListener implements OnClickListener{
        private Button lastButton;

        public OnTextButtonClickListener(Button b){
            lastButton=b;
            if(lastButton!=null){
                lastButton.setTextColor(getResources().getColor(R.color.toolbarTextAccent));
            }

        }
        @Override
        public void onClick(View v) {
            String last=lastButton.getText().toString().trim();
            lastButton.setTextColor(getResources().getColor(R.color.toolbarTextNormal));
            lastButton= (Button) v;
            lastButton.setTextColor(getResources().getColor(R.color.toolbarTextAccent));
            String target=lastButton.getText().toString().trim();
            notifyNavClick(last,target);
        }
    }

    public void addListener(MainToolbarListener listener){
        synchronized (listenerList){
            listenerList.add(listener);
        }
    }

    public void removeListener(MainToolbarListener listener){
        synchronized (listenerList){
            listenerList.remove(listener);
        }
    }

    public void clearListener(){
        synchronized (listenerList){
            listenerList.clear();
        }
    }

    private void notifyNavClick(String last,String target){
        synchronized (listenerList){
            for(MainToolbarListener listener:listenerList){
                listener.onNavClick(last,target);
            }
        }
    }

    private void notifyMenuClick(){
        synchronized (listenerList){
            for(MainToolbarListener listener:listenerList){
                listener.onMenuClick();
            }
        }
    }
}
