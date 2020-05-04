package com.baokaicong.android.bmusic.util.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.bean.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class DownloadSQLUtil extends SQLiteOpenHelper {
    private static final String TABLE_NAME="t_download_info";
    private static final String CREATE_TABLE="create table "+TABLE_NAME+"(" +
            "_id integer primary key autoincrement," +
            "mid," +
            "path," +
            "date," +
            "userId)";
    private static final String LIST_SQL="select * from "+TABLE_NAME;
    private static final String QUERY_SQL="select * from "+TABLE_NAME+" where mid=?";
    private static final String INSERT_SQL="insert into "+TABLE_NAME+"(mid,path,date,userId) values(?,?,?,?)";
    private static final String UPDATE_SQL="update "+TABLE_NAME+" set path=?,date=?,userId=? where mid=?";
    private static final String DELETE_SQL="delete from "+TABLE_NAME+" where mid=?";

    public DownloadSQLUtil(Context context) {
        super(context, TABLE_NAME, null, BMContext.instance().getVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<DownloadInfo> listAll(){
        Cursor cursor=getReadableDatabase().rawQuery(LIST_SQL,null);
        List<DownloadInfo> list=new ArrayList<>();
        DownloadInfo info;
        while (cursor.moveToNext()){
            info=new DownloadInfo()
                    .setId(cursor.getInt(0))
                    .setMid(cursor.getString(1))
                    .setPath(cursor.getString(2))
                    .setDate(cursor.getString(3))
                    .setUserId(cursor.getString(4));
            list.add(info);
        }
        if(cursor!=null){
            cursor.close();
        }

        return list;
    }

    public DownloadInfo getInfo(String mid){
        Cursor cursor=getReadableDatabase().rawQuery(QUERY_SQL,new String[]{mid});
        if(cursor.moveToNext()){
            DownloadInfo info=new DownloadInfo()
                    .setId(cursor.getInt(0))
                    .setMid(cursor.getString(1))
                    .setPath(cursor.getString(2))
                    .setDate(cursor.getString(3))
                    .setUserId(cursor.getString(4));
            return info;
        }
        if(cursor!=null){
            cursor.close();
        }
        return null;
    }

    public void insertInfo(DownloadInfo info){
        Cursor cursor=getReadableDatabase().rawQuery("select _id from "+TABLE_NAME+" where mid=?",new String[]{info.getMid()+""});
        if(cursor.moveToNext()){
            getWritableDatabase().execSQL(UPDATE_SQL, new String[]{info.getPath(), info.getDate(),info.getUserId(),info.getMid()});
        }else {
            getWritableDatabase().execSQL(INSERT_SQL, new String[]{info.getMid(),info.getPath(), info.getDate(),info.getUserId()});
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    public void deleteInfo(String mid){
        getWritableDatabase().execSQL(DELETE_SQL,new String[]{mid});
    }

    public int countAll(){
        Cursor cursor=getReadableDatabase().rawQuery("select count(mid) from "+TABLE_NAME,null);
        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }
        if(cursor!=null){
            cursor.close();
        }
        return 0;
    }
}
