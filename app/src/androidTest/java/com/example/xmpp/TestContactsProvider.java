package com.example.xmpp;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.provider.ContacksProvider;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class TestContactsProvider {
    @Test
    public void testinsert() {
        ContentValues values =new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT,"247140155@qq.com");
        values.put(ContactOpenHelper.ContactTable.NICKNAME,"老吴");
        values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
        values.put(ContactOpenHelper.ContactTable.PINYIN,"laowu");

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().insert(ContacksProvider.URI_CONTACT,values);
    }
    @Test
    public void testDelete() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().delete(ContacksProvider.URI_CONTACT,ContactOpenHelper.ContactTable.ACCOUNT
                + "=?",new String[]{"247140155@qq.com"});
    }
    @Test
    public void testUpdate() {
        ContentValues values =new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT,"247140155@qq.com");
        values.put(ContactOpenHelper.ContactTable.NICKNAME,"我是老吴");
        values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
        values.put(ContactOpenHelper.ContactTable.PINYIN,"woshilaowu");

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        appContext.getContentResolver().update(ContacksProvider.URI_CONTACT,values,ContactOpenHelper.ContactTable.ACCOUNT
                + "=?",new String[]{"247140155@qq.com"});
    }
    @Test
    public void testQuery() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Cursor c =appContext.getContentResolver().query(ContacksProvider.URI_CONTACT,null,null,null,null,null);
        int columncount = c.getColumnCount();
        while(c.moveToNext()){
            for(int i=0;i<columncount;i++){
                System.out.print(c.getString(i)+"   ");
            }
            System.out.println("");
        }
    }
    @Test
    public void testpinyin(){

        String pinginString=PinyinHelper.convertToPinyinString("蒋隆昌","--", PinyinFormat.WITHOUT_TONE);
        System.out.println(pinginString);
    }
}
