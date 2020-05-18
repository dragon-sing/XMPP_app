package com.example.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xmpp.dbhelper.ContactOpenHelper;

public class ContacksProvider extends ContentProvider {
    public static final String authorities = ContacksProvider.class.getCanonicalName();//得到类的一个完整路径

    public static final int CONTACT=1;

    //地址匹配对象
    static UriMatcher mUriMatcher;
    //对应联系人表的一个url常量
    public  static Uri URI_CONTACT =  Uri.parse("content://"+authorities+"/contact");
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //添加一个匹配规则
        mUriMatcher.addURI(authorities,"/contact",CONTACT);
        //content://com.example.xmpp.provider.ContacksProvider -->contact
    }

    private ContactOpenHelper mHelper;


    @Override
    public boolean onCreate() {
        mHelper = new ContactOpenHelper(getContext());
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



    /*--------                curd begin             ---*/
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //数据是存在sqlite --> 创建db，简历表，
        int code=mUriMatcher.match(uri);
        switch(code){
            case CONTACT:
                SQLiteDatabase db=mHelper.getWritableDatabase();
                long id=db.insert(ContactOpenHelper.T_CONTACT,"",values);
                if(id != -1){
                    System.out.println("-------------------ContactsProvider--------insertsuccess-----------");
                    //拼接最新的uri
                    //content://com.example.xmpp.provider.ContacksProvider -->contact
                    uri=ContentUris.withAppendedId(uri,id);
                    //通知ContentObservers数据改变了
                    getContext().getContentResolver().notifyChange(ContacksProvider.URI_CONTACT,null);//为空所有都可以收到，指定某一个observer可以收到
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code=mUriMatcher.match(uri);
        int deleteCount=0;
        switch(code){
            case CONTACT:
                SQLiteDatabase db=mHelper.getWritableDatabase();
                //影响的行数
                deleteCount=db.delete(ContactOpenHelper.T_CONTACT,selection,selectionArgs);
                if(deleteCount>0){
                    System.out.println("--------ContactsProvider----deleteSucess-------");
                    getContext().getContentResolver().notifyChange(ContacksProvider.URI_CONTACT,null);//为空所有都可以收到，指定某一个observer可以收到
                }
                break;
            default:
                break;
        }
        return deleteCount;
    }
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code=mUriMatcher.match(uri);
        Cursor cursor=null;
        switch(code){
            case CONTACT:
                SQLiteDatabase db=mHelper.getReadableDatabase();
                cursor=db.query(ContactOpenHelper.T_CONTACT,projection,selection,selectionArgs,null,null,sortOrder);
                System.out.println("--------ContactsProvider----querySucess----------");
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updateCount=0;
        int code=mUriMatcher.match(uri);
        switch(code){
            case CONTACT:
                SQLiteDatabase db=mHelper.getWritableDatabase();
                updateCount=db.update(ContactOpenHelper.T_CONTACT,values,selection,selectionArgs);
                if(updateCount>0){
                    System.out.println("-------------------ContactsProvider--------updatesuccess--------");
                    getContext().getContentResolver().notifyChange(ContacksProvider.URI_CONTACT,null);//为空所有都可以收到，指定某一个observer可以收到
                }
                break;
            default:
                break;
        }
        return updateCount;
    }
}
