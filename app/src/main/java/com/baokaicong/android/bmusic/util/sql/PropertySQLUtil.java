package com.baokaicong.android.bmusic.util.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baokaicong.android.bmusic.BMContext;

public class PropertySQLUtil extends SQLiteOpenHelper {
    private static final String TABLE_NAME="t_property";
    private static final String CREATE_TABLE="create table t_property(" +
            "_id integer primary key autoincrement," +
            "name," +
            "value)";
    private static final String QUERY_SQL="select * from t_property where name=?";
    private static final String INSERT_SQL="insert into t_property(name,value) values(?,?)";
    private static final String UPDATE_SQL="update t_property set value=? where name=?";
    private static final String DELETE_SQL="delete from t_property where name=?";
    public PropertySQLUtil(Context context) {
        super(context, TABLE_NAME, null, BMContext.instance().getVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getProperty(String name){
        Cursor cursor=getReadableDatabase().rawQuery(QUERY_SQL,new String[]{name});
        String value=null;
        if(cursor.moveToNext()){
            value=cursor.getString(2);
        }
        if(cursor!=null){
            cursor.close();
        }
        return value;
    }

    public void updateProperty(String name,String value){
        insertProperty(name,value);
    }

    public void insertProperty(String name,String value){
        Cursor cursor=getReadableDatabase().rawQuery("select _id from t_property where name=?",new String[]{name});
        if(cursor.moveToNext()){
            getWritableDatabase().execSQL(UPDATE_SQL, new String[]{value, name});
        }else {
            getWritableDatabase().execSQL(INSERT_SQL, new String[]{name, value});
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    public void deleteProperty(String name){
        getWritableDatabase().execSQL(DELETE_SQL,new String[]{name});
    }
}
