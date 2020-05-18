package com.example.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xmpp.dbhelper.SmsOpenHelper;

public class SmsProvider extends ContentProvider {
    private static final String AUTHORITIES= SmsProvider.class.getCanonicalName();

    static UriMatcher mUriMatcher;
    public static  Uri  URI_SMS=Uri.parse("content://"+AUTHORITIES+"/sms");
    private static final int SMS = 1;

    static {
        mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        //添加匹配规则
        mUriMatcher.addURI(AUTHORITIES,"/sms", SMS);
    }

    private SmsOpenHelper mHelper;

    @Override
    public boolean onCreate() {

        //创建表，创建数据库
        mHelper = new SmsOpenHelper(getContext());
        if(mHelper!=null){
            return true;
        }
        return false;
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    /*------------------------crud---begin----------------------------------------*/
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = 0 ;
        switch (mUriMatcher.match(uri)){
            case SMS:
                id= mHelper.getWritableDatabase().insert(SmsOpenHelper.T_SMS,"",values);
                if(id>0){
                    System.out.println("-------------SmsProvider insertSuccess--------------");
                    uri = ContentUris.withAppendedId(uri,id);
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
            default:
                break;
        }

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletecount = 0 ;
        switch (mUriMatcher.match(uri)){
            case SMS:
                deletecount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS,selection,selectionArgs);
                if(deletecount>0){
                    System.out.println("--------------------SmsProvider deleteSuccess-----------------");
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
            default:
                break;
        }
        return deletecount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updateCount=0;
        switch (mUriMatcher.match(uri)){
            case SMS:
                updateCount = mHelper.getWritableDatabase().update(SmsOpenHelper.T_SMS,values,selection,selectionArgs);
                if(updateCount>0){
                    System.out.println("--------------------SmsProvider updateSuccess-----------------");
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
            default:
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)){
            case SMS:
                cursor = mHelper.getReadableDatabase().query(SmsOpenHelper.T_SMS,projection,selection,selectionArgs,null,null,sortOrder);
                System.out.println("--------------------SmsProvider querySuccess-----------------");
                break;
            default:
                break;
        }

        return cursor;
    }
}
