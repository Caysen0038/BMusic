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

import java.util.List;

public class MenuListAdapter extends BaseAdapter {
    private List<MusicMenu> musicMenuList;
    private Context context;
    private LayoutInflater layoutInflater;
    private class Item{
        public ImageView icon;
        public TextView name;
        public TextView count;
    }
    public MenuListAdapter(Context context, List<MusicMenu> musicMenus){
        this.musicMenuList=musicMenus;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return musicMenuList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item=new Item();
        convertView = layoutInflater.inflate(R.layout.item_home_menu, null);
        item.icon=convertView.findViewById(R.id.music_menu_icon);
        item.name=convertView.findViewById(R.id.music_menu_name);
        item.count=convertView.findViewById(R.id.music_menu_count);
        item.name.setText(musicMenuList.get(position).getName());
        item.count.setText(musicMenuList.get(position).getCount()+"首");
        return convertView;
    }
}
