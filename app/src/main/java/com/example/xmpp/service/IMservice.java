package com.example.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.jivesoftware.smack.XMPPConnection;

public class IMservice  {
    public  static XMPPConnection conn;
    public static String mCurAccout;//当前登录用户的id

}
