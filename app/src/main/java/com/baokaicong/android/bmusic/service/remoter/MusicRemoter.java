package com.baokaicong.android.bmusic.service.remoter;

import com.baokaicong.android.bmusic.bean.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicRemoter implements MediaRemoter<Music> {
    private RemoteReceiver remoteReceiver;
    private List<RemoteReceiver> remoteReceiverList;
    public MusicRemoter(RemoteReceiver remoteReceiver){
        remoteReceiverList =new ArrayList<>();
        this.remoteReceiver = remoteReceiver;
    }

//    private static class Holder{
//        private static final MusicRemoter instance=new MusicRemoter();
//    }
//
//    public static MusicRemoter instance(){
//        return Holder.instance;
//    }

    @Override
    public void load(List<Music> list) {
//        synchronized (remoteReceiverList){
////            for(RemoteReceiver remoteReceiver:remoteReceiverList){
////                remoteReceiver.load(url,loop);
////            }
////        }
        remoteReceiver.load(list);
    }

    @Override
    public void add(Music m, int i) {
        remoteReceiver.add(m,i);
    }

    @Override
    public void play() {
//        synchronized (remoteReceiverList){
//            for(RemoteReceiver remoteReceiver:remoteReceiverList){
//                remoteReceiver.play();
//            }
//        }
        remoteReceiver.play();
    }

    @Override
    public void pause() {
//        synchronized (remoteReceiverList){
//            for(RemoteReceiver remoteReceiver:remoteReceiverList){
//                remoteReceiver.pause();
//            }
//        }
        remoteReceiver.pause();
    }

    @Override
    public void next() {
        remoteReceiver.next();
    }

    @Override
    public void pre() {
        remoteReceiver.pre();
    }


    @Override
    public void jump(int rate) {
//        synchronized (remoteReceiverList){
//            for(RemoteReceiver remoteReceiver:remoteReceiverList){
//                remoteReceiver.jump(rate);
//            }
//        }
        remoteReceiver.jump(rate);
    }


//
//    @Override
//    public void addMedia(RemoteReceiver remoteReceiver) {
////        synchronized (remoteReceiverList){
////            remoteReceiverList.add(remoteReceiver);
////        }
//
//    }
//
//    @Override
//    public void removeMedia(RemoteReceiver remoteReceiver) {
//        remoteReceiverList.remove(remoteReceiver);
//    }




}
