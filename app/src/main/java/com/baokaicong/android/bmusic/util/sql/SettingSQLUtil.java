package com.baokaicong.android.bmusic.util.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.BMContext;

public class SettingSQLUtil extends SQLiteOpenHelper {
    private static final String TABLE_NAME="t_setting";
    private static final String CREATE_TABLE="create table t_setting(" +
            "_id integer primary key autoincrement," +
            "name," +
            "value)";
    private static final String QUERY_SQL="select * from t_setting where name=?";
    private static final String INSERT_SQL="insert into t_setting(name,value) values(?,?)";
    private static final String UPDATE_SQL="update t_setting set value=? where name=?";
    private static final String DELETE_SQL="delete from t_setting where name=?";
    public SettingSQLUtil(Context context) {
        super(context, TABLE_NAME, null, BMContext.instance().getVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getSetting(String name){
        Cursor cursor=getReadableDatabase().rawQuery(QUERY_SQL,new String[]{name});
        String value=null;
        if(cursor.moveToNext()){
            value=cursor.getString(2);
        }
        cursor.close();
        return value;
    }

    public void updateSetting(String name,String value){
        getWritableDatabase().execSQL(UPDATE_SQL,new String[]{value,name});
    }

    public void insertSetting(String name,String value){
        Cursor cursor=getReadableDatabase().rawQuery("select _id from t_setting where name=?",new String[]{name});
        if(cursor.moveToNext()){
            updateSetting(name,value);
        }else {
            getWritableDatabase().execSQL(INSERT_SQL, new String[]{name, value});
        }
        cursor.close();
    }

    public void deleteSetting(String name){
        getWritableDatabase().execSQL(DELETE_SQL,new String[]{name});
    }
}
