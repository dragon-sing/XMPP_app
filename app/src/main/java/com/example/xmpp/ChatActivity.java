package com.example.xmpp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmpp.Utils.ThreadUtils;
import com.example.xmpp.Utils.ToastUtils;
import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.provider.ContacksProvider;
import com.example.xmpp.provider.SmsProvider;
import com.example.xmpp.service.IMservice;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String CLICKACCOUNT = "clickAccount" ;
    public static final String CLICKNICKNAME = "clickNickName";

    private String mClickAccount;
    private String mclickNickName;
    private TextView mTitle;
    private ListView mListView;
    private EditText mEtBody;
    private Button mBtnSend;
    private CursorAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        initView();
        initData();
        initListener();

    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.title);
        mListView = (ListView)findViewById(R.id.listView);
        mEtBody = (EditText) findViewById(R.id.et_body);
        mBtnSend = (Button)findViewById(R.id.btn_send);

        //设置title
        mTitle.setText("与"+mclickNickName+"聊天中");

    }


    private void init() {
        registerContentProvider();
        mClickAccount = getIntent().getStringExtra(ChatActivity.CLICKACCOUNT);
        mclickNickName = getIntent().getStringExtra(ChatActivity.CLICKNICKNAME);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initData() {
        setAdapterOrNotify();
    }

    private void setAdapterOrNotify() {
        //1.首先判断是否存在adpter
        if(mAdapter!=null){
            //刷新
            Cursor cursor =  mAdapter.getCursor();
            cursor.requery();
            mListView.setSelection(cursor.getCount()-1);
            return ;
        }
        ThreadUtils.runInThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                final Cursor c =getContentResolver().query(SmsProvider.URI_SMS,null,null,null,null,null);
                //如果没有数据直接返回
                if(c.getCount()<1){
                    return ;
                }

                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //CursorAdapter getview -- newView -- bindview
                        mAdapter = new CursorAdapter(ChatActivity.this,c ){
                            public static final int RECEIVE = 1;
                            public static final int SEND = 0;
                            /*
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                TextView tv = new TextView(context);
                                //
                                return tv;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                                TextView tv = (TextView) view;
                                String body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                tv.setText(body);
                            }
                            */

                            @Override
                            public int getItemViewType(int position){
                                //接受--当前账号不等于账号创建者，就是接受
                                //发送--当期那账号是账号创建者
                                c.moveToPosition(position);
                                //取出消息创建者
                                String fromAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
                                if(!IMservice.mCurAccout.equals(fromAccount)){//接受
                                    return RECEIVE;
                                }else{//发送
                                    return SEND;
                                }
                                //return super.getItemViewType(position);
                            }
                            @Override
                            public int getViewTypeCount(){
                                return super.getViewTypeCount()+1;
                            }

                            @Override
                            public View getView (int postion ,View convertView,ViewGroup parent){
                                ViewHolder holder;
                                if(getItemViewType(postion)==RECEIVE){

                                    if(convertView==null){
                                        convertView = View.inflate(ChatActivity.this,R.layout.item_chat_receive,null);
                                        holder=new ViewHolder();
                                        convertView.setTag(holder);
                                        //holder赋值
                                        holder.time=(TextView) convertView.findViewById(R.id.time);
                                        holder.body=(TextView) convertView.findViewById(R.id.content);
                                        holder.head=(ImageView) convertView.findViewById(R.id.head);
                                    }
                                    else{
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                }
                                else{

                                        if(convertView==null){
                                            convertView = View.inflate(ChatActivity.this,R.layout.item_chat_send,null);
                                            holder=new ViewHolder();
                                            convertView.setTag(holder);
                                            //holder赋值
                                            holder.time=(TextView) convertView.findViewById(R.id.time);
                                            holder.body=(TextView) convertView.findViewById(R.id.content);
                                            holder.head=(ImageView) convertView.findViewById(R.id.head);
                                        }
                                        else{
                                            holder = (ViewHolder) convertView.getTag();
                                        }
                                }
                                //得到数据，展示数据
                                c.moveToPosition(postion);

                                String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                                String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((Long.parseLong(time))));
                                holder.time.setText(formatTime);
                                holder.body.setText(body);
                                return super.getView(postion,convertView,parent);
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                            }
                            class ViewHolder{
                                TextView body;
                                TextView time;
                                ImageView head;
                            }

                        };
                        mListView.setAdapter(mAdapter);
                        mListView.setSelection(mAdapter.getCount()-1);
                    }
                });

            }
        });
    }

    private void initListener() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                final String body = mEtBody.getText().toString();
                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();

                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //1获取消息管理者

                            ChatManager chatManager = IMservice.conn.getChatManager();
                            //2 创建聊天对象
                            //chatManager.createChat("发送对象JID",消息监听者);
                            MyMessageListener messageListener = new MyMessageListener();
                            Chat chat = chatManager.createChat(mClickAccount,messageListener);
                            //3发送信息
                            Message msg = new Message();
                            msg.setFrom(IMservice.mCurAccout);
                            msg.setTo(mClickAccount);
                            msg.setBody(body);
                            msg.setType(Message.Type.chat);
                            msg.setProperty("key","value");//额外属性

                            chat.sendMessage(msg);

                            //保存消息
                            //发送消息 肯定是我发送别人
                            saveMessage(mClickAccount,msg);


                            //4清空输出框
                            ThreadUtils.runInUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEtBody.setText("");
                                }
                            });
                        }catch (XMPPException e){
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    };
    /*
    保存消息
        contentResolver -->contentProvider ->sqlite

     */
    private void saveMessage(String sessionAccount, Message msg) {

        ContentValues values =new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,msg.getFrom());
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,msg.getTo());
        values.put(SmsOpenHelper.SmsTable.BODY,msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
        values.put(SmsOpenHelper.SmsTable.TYPE,msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,sessionAccount);

        getContentResolver().insert(SmsProvider.URI_SMS,values);

    }
    class MyMessageListener implements MessageListener{

        @Override
        public void processMessage(Chat chat, Message message) {
            String body = message.getBody();
            if(body==null){
                return ;
            }
            ToastUtils.showToastSafe(ChatActivity.this,body);
            System.out.println("body"+message.getBody());
            System.out.println(message.getFrom());
            System.out.println(message.getTo());

            //收到消息保存消息
            String participant = chat.getParticipant();
            saveMessage(participant,message);
        }
    }
    MyContentObserver mMyContentObserver  =  new MyContentObserver(new Handler());

    @Override
    protected void onDestroy(){
        unRegisterContentProvider();
        super.onDestroy();
    }

    /*=====================================使用contentobserver 时刻监听数据改变===================================*/
    /*注册监听*/
    public void registerContentProvider(){
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS,true,mMyContentObserver);

    }
    public void unRegisterContentProvider(){
        getContentResolver().unregisterContentObserver(mMyContentObserver);

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
        @Override
        public void onChange (boolean selfchange , Uri uri){
            setAdapterOrNotify();
            super.onChange(selfchange,uri);
        }

    }



}
