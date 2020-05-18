package com.example.xmpp.Utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToastSafe(final Context context, final String text){
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
