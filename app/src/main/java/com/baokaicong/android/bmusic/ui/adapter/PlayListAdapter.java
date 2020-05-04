package com.baokaicong.android.bmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Music;

import java.util.List;

public class PlayListAdapter extends BaseAdapter {
    private List<Music> musicsList;
    private Context context;
    private LayoutInflater layoutInflater;
    private class Item{
        public ImageView delete;
        public TextView name;
    }
    public PlayListAdapter(Context context, List<Music> musics){
        this.musicsList=musics;
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return musicsList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item=new Item();
        convertView = layoutInflater.inflate(R.layout.item_play_list, null);
        item.name=convertView.findViewById(R.id.item_name);
        item.delete=convertView.findViewById(R.id.item_delete);
        item.name.setText(musicsList.get(position).getName());
        return convertView;
    }
}
