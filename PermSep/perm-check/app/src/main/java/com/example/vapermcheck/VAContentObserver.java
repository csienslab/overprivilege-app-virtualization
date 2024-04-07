package com.example.vapermcheck;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class VAContentObserver extends ContentObserver {
    Context mContext;
    Handler mHandler;
    public static HashMap mcheckMap;

    public VAContentObserver(Context context, Handler handler, HashMap checkMap) {
        super(handler);
        mContext = context;
        mHandler = handler;
        mcheckMap = checkMap;
        List<String> hlst = new ArrayList<String>();
        mcheckMap.put("hpkg", hlst);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onChange(boolean selfChange){
        Log.e("VAContentObserverLog", "First Inside VAContentObserver");
        XposedBridge.log("First Inside VAContentObserver");
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri CONTENT_URI = Uri.parse("content://" + VADatabase.AUTHORITY + VADatabase.PATH_PLUGIN);

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    XposedBridge.log("Inside VAContentObserver");
                    @SuppressLint("Range") String uid = cursor.getString(cursor.getColumnIndex(VADatabase.Plugin.UID));
                    @SuppressLint("Range") String pid = cursor.getString(cursor.getColumnIndex(VADatabase.Plugin.PID));
                    @SuppressLint("Range") String pkgName = cursor.getString(cursor.getColumnIndex(VADatabase.Plugin.NAME));
                    List<String> lst = new ArrayList<String>();
                    lst.add(pkgName);
                    lst.add(uid);

                    int key = Integer.parseInt(pid);

                    if (mcheckMap.containsKey(key)){
                        mcheckMap.replace(key, lst);
                    } else {
                        mcheckMap.put(key, lst);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.e("VAContentObserverLog", String.valueOf(mcheckMap));

    }

}