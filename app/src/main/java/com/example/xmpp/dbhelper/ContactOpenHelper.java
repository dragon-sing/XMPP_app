package com.example.xmpp.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class ContactOpenHelper extends SQLiteOpenHelper {
    public static  final String T_CONTACT ="t_contact";
    public class ContactTable implements BaseColumns{//默认给我们添加列 _id
        /*

         */
        public static  final String ACCOUNT="account";
        public static  final String NICKNAME="nickname";
        public static  final String AVATAR="avatar";
        public static  final String PINYIN="pinyin";


    }
    public ContactOpenHelper(@Nullable Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+T_CONTACT+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ContactTable.ACCOUNT+" TEXT, "+
                ContactTable.NICKNAME+" TEXT, "+
                ContactTable.AVATAR+" TEXT, "+
                ContactTable.PINYIN+" TEXT);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
