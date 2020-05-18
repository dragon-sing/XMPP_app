package com.example.xmpp.Utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class PinyinUtil {
    public static String getPinyin(String str){
        return PinyinHelper.convertToPinyinString(str,"", PinyinFormat.WITHOUT_TONE);
    }
}
