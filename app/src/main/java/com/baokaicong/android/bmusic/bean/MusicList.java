package com.baokaicong.android.bmusic.bean;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicList {
    private String id;
    private List<Music> list;
    public MusicList(){
        list=new ArrayList<>();
        id=System.currentTimeMillis()+"";
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
}
