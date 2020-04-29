package com.zhang.autotouch.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void show(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }
}
