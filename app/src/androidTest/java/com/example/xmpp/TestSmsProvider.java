package com.example.xmpp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.provider.SmsProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestSmsProvider  {
    @Test
    public void testinsert() {
        ContentValues values =new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,"247140155@qq.com");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,"zhp");
        values.put(SmsOpenHelper.SmsTable.BODY,"今晚约吗");
        values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
        values.put(SmsOpenHelper.SmsTable.TYPE,"chat");
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,"zhp");


        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().insert(SmsProvider.URI_SMS,values);
    }
    @Test
    public void testDelete() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().delete(SmsProvider.URI_SMS,SmsOpenHelper.SmsTable.FROM_ACCOUNT
                + "=?",new String[]{"247140155@qq.com"});
    }
    @Test
    public void testUpdate() {
        ContentValues values =new ContentValues();

        values.put(SmsOpenHelper.SmsTable.BODY,"今晚约吗??????????????sb");
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,"zhp");

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().update(SmsProvider.URI_SMS,values,SmsOpenHelper.SmsTable.FROM_ACCOUNT
                + "=?",new String[]{"247140155@qq.com"});
    }
    @Test
    public void testQuery() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Cursor c =appContext.getContentResolver().query(SmsProvider.URI_SMS,null,null,null,null,null);
        int columncount = c.getColumnCount();
        while(c.moveToNext()){
            for(int i=0;i<columncount;i++){
                System.out.print(c.getString(i)+"   ");
            }
            System.out.println("");
        }
    }
}
