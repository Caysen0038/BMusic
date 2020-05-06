package com.baokaicong.android.bmusic.bean;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MusicList {
    private String id;
    private List<Music> list;
    public MusicList(){
        list=new ArrayList<>();
    }

    public int size(){
        return list.size();
    }

    public Music get(int i){
        return list.get(i);
    }

    public void add(List<Music> list){
        this.list=list;
    }

    public void remove(Music m){
        list.remove(m);
    }

    public void remove(int i){
        list.remove(i);
    }

    public void add(Music m){
        if(!list.contains(m)){
            list.add(m);
        }
    }

    public void add (Music m,int i){
        if(list.contains(m)){
            list.remove(m);
        }
        if(i>list.size() || i<0){
            list.add(m);
        }else{
            list.add(i, m);
        }
    }

    public void clear(){
        list.clear();
    }

    public boolean contains(Music music){
        return this.list.contains(music);
    }

    public int getMusicPosition(Music music){
        return this.list.indexOf(music);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null)
            return false;
        if(!(obj instanceof MusicList))
            return false;
        return ((MusicList)obj).getId().equals(this.id);
    }


}
