package com.example.hp.tiptip;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "myDatabase.db";
    public static final String TABLE_NAME = "chat_record_of_friend";

    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+TABLE_NAME+" ("+
                "record_id integer primary key autoincrement, "+
                "sender_id varchar(20), "+
                "receiver_id varchar(20), "+
                "content varchar(255), "+
                "content_type varchar(20), "+
                "build_time varchar(40) "+
            ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
