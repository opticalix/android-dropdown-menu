package com.opticalix.dropdown_lib;

/**
 * Created by Opticalix on 2015/4/21.
 */

import android.util.Log;



/**
 * Log Manager
 * Format like this: Log.d("Testing", String.format("x0 = %5.2f, x1=%5.2f", x0, x1));
 * Be careful, long/short/int use %d
 */
public class LogUtils {

    private LogUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = getAppShortName();
    private static String prefix = "";

    public static String getAppShortName(){
        return BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf(".") + 1, BuildConfig.APPLICATION_ID.length());
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, prefix + msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, prefix + msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, prefix + msg);
    }

    public static void w(String msg) {
        if (isDebug)
            Log.w(TAG, prefix + msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, prefix + msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(Object object, String msg) {
        if (isDebug)
            Log.i(object.getClass().getSimpleName(), prefix + msg);
    }

    public static void d(Object object, String msg) {
        if (isDebug)
            Log.d(object.getClass().getSimpleName(), prefix + msg);
    }

    public static void e(Object object, String msg) {
        if (isDebug)
            Log.e(object.getClass().getSimpleName(), prefix + msg);
    }

    public static void e(Object object, String msg, Throwable e) {
        if (isDebug)
            Log.e(object.getClass().getSimpleName(), prefix + msg, e);
    }

    public static void w(Object object, String msg) {
        if (isDebug)
            Log.w(object.getClass().getSimpleName(), prefix + msg);
    }

    public static void v(Object object, String msg) {
        if (isDebug)
            Log.v(object.getClass().getSimpleName(), prefix + msg);
    }


    public static void dString(Object o, String... s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("String args: ");
        for (String ss : s) {
            stringBuilder.append(String.format("%s", ss));
            stringBuilder.append("\t");
        }
        if (isDebug) {
            Log.d(o.getClass().getSimpleName(), stringBuilder.toString());
        }
    }

    public static void dInt(Object o, Integer... i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Integer args: ");
        for (Integer ii : i) {
            stringBuilder.append(String.format("%d", ii));
            stringBuilder.append("\t");
        }
        if (isDebug) {
            Log.d(o.getClass().getSimpleName(), stringBuilder.toString());
        }
    }
}