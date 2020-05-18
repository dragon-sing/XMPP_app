package com.example.xmpp.Utils;

import android.os.Handler;

public class ThreadUtils {
    /**子线程执行task*/
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }

    /**
     * 主线程里的一个handler
     */
    public static Handler mHandler =new Handler();
    /**UI线程执行task*/


    public  static void  runInUIThread(Runnable task){
        mHandler.post(task);
    }
}
