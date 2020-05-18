package com.example.xmpp.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class SmsOpenHelper extends SQLiteOpenHelper {

    public static  final String T_SMS = "t_sms";
    public class SmsTable implements BaseColumns{

        public static final String  FROM_ACCOUNT = "from_account";
        public static final String  TO_ACCOUNT = "to_account";
        public static final String  BODY = "body";
        public static final String  STATUS = "status";
        public static final String  TYPE = "type";
        public static final String  TIME = "time";
        public static final String  SESSION_ACCOUNT = "session_account";
    }
    public SmsOpenHelper(@Nullable Context context) {
        super(context, "sms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE "+T_SMS+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                SmsTable.FROM_ACCOUNT+" TEXT,"+
                SmsTable.TO_ACCOUNT+" TEXT,"+
                SmsTable.BODY+" TEXT,"+
                SmsTable.STATUS+" TEXT,"+
                SmsTable.TYPE+" TEXT,"+
                SmsTable.TIME+" TEXT,"+
                SmsTable.SESSION_ACCOUNT+" TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
