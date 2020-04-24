package com.baokaicong.android.bmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.MusicMenu;

public class MusicMenuListAdapter extends BaseAdapter {
    private MusicMenu[] musicMenus;
    private Context context;
    private LayoutInflater layoutInflater;
    private class Item{
        public ImageView icon;
        public TextView name;
    }
    public MusicMenuListAdapter(Context context,MusicMenu[] musicMenus){
        this.musicMenus=musicMenus;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return musicMenus.length;
    }

    @Override
    public Object getItem(int position) {
        return musicMenus[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item=new Item();
        convertView = layoutInflater.inflate(R.layout.item_music_menu, null);
        item.icon=convertView.findViewById(R.id.music_menu_icon);
        item.name=convertView.findViewById(R.id.music_menu_name);
        item.name.setText(musicMenus[position].getName());
        return convertView;
    }
}
