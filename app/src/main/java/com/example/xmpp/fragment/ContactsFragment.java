package com.example.xmpp.fragment;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xmpp.ChatActivity;
import com.example.xmpp.R;
import com.example.xmpp.Utils.PinyinUtil;
import com.example.xmpp.Utils.ThreadUtils;
import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.provider.ContacksProvider;
import com.example.xmpp.service.IMservice;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    private ListView mlistView;
    private Roster roster;
    private CursorAdapter madapter;

    @Override
    public  void onCreate(Bundle savedInstanceState) {

        init();
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }
    private void init() {
        registerContentObserver();
    }
    private void initView(View view){
        mlistView = (ListView) view.findViewById(R.id.listView);
    }
    private void initListener(){
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = madapter.getCursor();
                c.moveToPosition(position);

                //拿到jid账号，发送消息的时候需要
                String account =c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                //拿到Nickname
                String nickname=c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));


                Intent intent = new Intent(getActivity(), ChatActivity.class);

                intent.putExtra(ChatActivity.CLICKACCOUNT,account);
                intent.putExtra(ChatActivity.CLICKNICKNAME,nickname);
                startActivity(intent);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private  void saveOrUpdateEntry(RosterEntry entry){
        ContentValues values =new ContentValues();
        String account=entry.getUser();
        //account = account.substring(0,account.indexOf("@"))+"@";
        values.put(ContactOpenHelper.ContactTable.ACCOUNT,account);
        String nickname=entry.getName();
        //处理昵称
        if(nickname==null||nickname.equals("")){
            nickname = account.substring(0,account.indexOf("@"));
        }
        values.put(ContactOpenHelper.ContactTable.NICKNAME,nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtil.getPinyin(account));

        //先update,后插入
        int updateCount=getActivity().getContentResolver().update(ContacksProvider.URI_CONTACT,values,ContactOpenHelper.ContactTable.ACCOUNT
                + "=?",new String[]{ account });
        if(updateCount<=0){//没有更新到任何记录
            getActivity().getContentResolver().insert(ContacksProvider.URI_CONTACT,values);

        }
        setOrUpdateAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setOrUpdateAdapter() {
        //判断adapter是否存在
        if(madapter!= null){
            madapter.getCursor().requery();
            return;
        }
        //对应查询记录
        final Cursor c = getActivity().getContentResolver().query(ContacksProvider.URI_CONTACT,null,null,null,null,null);
        //假如没有数据的时候
        if(c.getCount()<0){
            return ;
        }
        //设置adapter

        ThreadUtils.runInUIThread(new Runnable() {

            @Override
            public void run() {
                madapter = new CursorAdapter(getActivity(),c) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        View view =View.inflate(context,R.layout.item_contact,null);
                        return view;
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        ImageView tvHead = (ImageView)view.findViewById(R.id.head);
                        TextView tvAccount = (TextView) view.findViewById(R.id.account);
                        TextView tvNickname = (TextView)view.findViewById(R.id.nickname);


                        String account=cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                        String nickname=cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));


                        tvAccount.setText(account);
                        tvNickname.setText(nickname);
                    }
                };

                mlistView.setAdapter(madapter);
            }
        });
    }

    private void initData() {

        //开启线程同步花名册
        ThreadUtils.runInThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                //得到所有联系人
                roster = IMservice.conn.getRoster();
                //得到所有联系人
                final Collection<RosterEntry> entries= roster.getEntries();

                //打印所有的联系人
                for(RosterEntry entry:entries){
                    System.out.println(entry.toString());
                    System.out.println(entry.getUser());//jid
                    System.out.println(entry.getName());//nickname
//          System.out.println(entry.getStatus());
//          System.out.println(entry.getType());
                }
                //监听联系人的改变
                roster.addRosterListener(new MyRosterListener());

                for(RosterEntry entry:entries){
                    saveOrUpdateEntry(entry);
                }
            }
        });
        /**
         * us1: us1@test.com [Friends]
         * I/System.out: us1@test.com
         * I/System.out: us1
         * I/System.out: null
         * I/System.out: to
         */
    }
    class MyRosterListener implements RosterListener{

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void entriesAdded(Collection<String> addresses) {//联系人添加了
            System.out.println("-----------entriesAdded-------------");
            //对应更新数据库
            for (String address : addresses ){
                RosterEntry entry= roster.getEntry(address);
                saveOrUpdateEntry(entry);
            }


        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void entriesUpdated(Collection<String> addresses) {//联系人修改了
            System.out.println("-----------entriesUpdated-------------");
            //对应更新数据库
            for (String address : addresses ){
                RosterEntry entry= roster.getEntry(address);
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {//联系人删除了
            System.out.println("-----------entriesDeleted-------------");
            //对应更新数据库
            for (String account : addresses ){

                getActivity().getContentResolver().delete(ContacksProvider.URI_CONTACT,ContactOpenHelper.ContactTable.ACCOUNT
                        + "=?",new String[]{account});

            }
        }

        @Override
        public void presenceChanged(Presence presence) {//联系人状态改变
            System.out.println("-----------entriesChanged-------------");
        }
    }
    @Override
    public void onDestroy() {
        unRegisterContentObserver();
        //移除rosterListener

        super.onDestroy();
    }


    //监听数据的记录改变

    /**注册监听*/
    MyContentObserver mMyContentObserver =new MyContentObserver(new Handler());

    public  void  registerContentObserver(){
        getActivity().getContentResolver().registerContentObserver(ContacksProvider.URI_CONTACT,true,
                mMyContentObserver);
    }

    public void  unRegisterContentObserver(){
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }
    class MyContentObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onchange(boolean selfChange, Uri uri){
            super.onChange(selfChange,uri);
            //更新adapter或者刷新
        }
    }
}
