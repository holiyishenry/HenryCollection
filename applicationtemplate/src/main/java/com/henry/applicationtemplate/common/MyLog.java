package com.henry.applicationtemplate.common;

import android.util.Log;

/**
 * Created by henry on 2015/1/10.
 * 统一管理整个软件的log信息
 */
public class MyLog {
    public static boolean isDebug = true;// 是否需要打印bug，也可以在application的onCreate函数里面初始化

    private MyLog(){
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg){
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg){
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg)
    {
         if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.v(tag, msg);
    }
}
