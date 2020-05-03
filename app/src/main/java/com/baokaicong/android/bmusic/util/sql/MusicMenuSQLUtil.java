package com.baokaicong.android.bmusic.util.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.bean.MusicMenu;
import java.util.ArrayList;
import java.util.List;

public class MusicMenuSQLUtil extends SQLiteOpenHelper {
    private static final String TABLE_NAME="t_music_menu";
    private static final String CREATE_TABLE="create table t_music_menu(" +
            "_id integer primary key autoincrement," +
            "meid," +
            "name," +
            "img," +
            "owner," +
            "date," +
            "count)";
    private static final String UPDATE_SQL="update t_music_menu set " +
            "name=?,img=?,owner=?,date=?,count=? where meid=?";
    private static final String INSERT_SQL="insert into t_music_menu(meid,name,img,owner,date,count) " +
            "values(?,?,?,?,?,?)";

    private static final String QUERY_OWNER_SQL="select * from t_music_menu where owner=?";

    private static final String QUERY_ONE_SQL="select * from t_music_menu where meid=? limit 1";

    private static final String DELETE_SQL="delete from t_music_menu where meid=?";


    public MusicMenuSQLUtil(Context context) {
        super(context, TABLE_NAME, null, BMContext.instance().getVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 获取用户菜单列表
     * @param owner
     * @return
     */
    public java.util.List<MusicMenu> listOwnerMenu(String owner){
        Cursor cursor=getReadableDatabase().rawQuery(
                QUERY_OWNER_SQL,new String[]{owner}
        );
        java.util.List<MusicMenu> list=new ArrayList<>();
        MusicMenu menu;
        while(cursor.moveToNext()){
            menu=new MusicMenu()
                    .setMeid(cursor.getString(1))
                    .setName(cursor.getString(2))
                    .setImg(cursor.getString(3))
                    .setOwner(owner)
                    .setDate(cursor.getString(5))
                    .setCount(cursor.getInt(6));
            list.add(menu);

        }
        cursor.close();
        return list;
    }

    /**
     * 获取单个菜单
     * @param meid
     * @return
     */
    public MusicMenu getMenu(String meid){
        Cursor cursor=getReadableDatabase().rawQuery(
                QUERY_ONE_SQL,new String[]{meid}
        );
        MusicMenu menu=null;
        if(cursor.moveToNext()){
            menu=new MusicMenu();
            menu.setMeid(meid)
                    .setName(cursor.getString(2))
                    .setImg(cursor.getString(3))
                    .setOwner(cursor.getString(4))
                    .setDate(cursor.getString(5))
                    .setCount(cursor.getInt(6));
        }
        cursor.close();
        return menu;
    }

    /**
     * 更新菜单
     * @param menu
     */
    public void updateMenu(MusicMenu menu){
        getWritableDatabase().execSQL(UPDATE_SQL,
                new Object[]{menu.getName(),
                                menu.getImg(),
                                menu.getOwner(),
                                menu.getDate(),
                                menu.getCount(),
                                menu.getMeid()
                                });
    }

    /**
     * 删除指定菜单
     * @param meid
     */
    public void deleteMenu(String meid){
        getWritableDatabase().execSQL(DELETE_SQL,new String[]{meid});
    }

    /**
     * 插入一个菜单
     * @param menu
     */
    public void insertMenu(MusicMenu menu){
        getWritableDatabase().execSQL(INSERT_SQL,
                new String[]{menu.getMeid(),
                            menu.getName(),
                            menu.getImg(),
                            menu.getOwner(),
                            menu.getDate()
                });
    }

    public void insertMenuList(List<MusicMenu> list){
        for(MusicMenu menu:list){
            insertOrUpdateMenu(menu);
        }
    }

    /**
     * 插入或者更新菜单
     * @param menu
     * @return
     */
    public int insertOrUpdateMenu(MusicMenu menu){
        MusicMenu temp=getMenu(menu.getMeid());
        if(temp==null){
            insertMenu(menu);
            return 1;
        }else{
            updateMenu(menu);
            return 2;
        }
    }
}
