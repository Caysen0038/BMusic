package com.baokaicong.android.bmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;
import com.baokaicong.android.bmusic.bean.MusicMenu;

public class MusicListAdapter extends BaseAdapter {
    private Music[] musics;
    private Context context;
    private LayoutInflater layoutInflater;
    private class Item{
        public TextView name;
        public TextView singer;
        public Button addButton;
    }
    public MusicListAdapter(Context context, Music[] musics){
        this.musics=musics;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return musics.length;
    }

    @Override
    public Object getItem(int position) {
        return musics[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item=new Item();
        convertView = layoutInflater.inflate(R.layout.item_menu_music, null);
        item.name=convertView.findViewById(R.id.music_name);
        item.singer=convertView.findViewById(R.id.music_singer);
        item.name.setText(musics[position].getName());
        item.singer.setText(musics[position].getSinger());
//        item.addButton=convertView.findViewById(R.id.music_add);
//        item.addButton.setOnClickListener((v)->{});
        return convertView;
    }

}
