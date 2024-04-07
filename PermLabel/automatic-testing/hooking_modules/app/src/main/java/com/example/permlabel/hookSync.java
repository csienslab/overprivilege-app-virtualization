package com.example.permlabel;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookSync implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.binaryguilt.completemusicreadingtrainer") &&
                !lpparam.packageName.equals("com.downdogapp") &&
                !lpparam.packageName.equals("com.economist.lamarr") &&
                !lpparam.packageName.equals("com.klook") &&
                !lpparam.packageName.equals("com.perfectlyhappy.meditation") &&
                !lpparam.packageName.equals("com.xiaomi.smarthome") &&
                !lpparam.packageName.equals("com.zhiliaoapp.musically.go")
        ) {
            return;
        }

        XposedHelpers.findAndHookMethod("android.content.ContentResolver", lpparam.classLoader, "addPeriodicSync", Account.class, String.class, Bundle.class, long.class,
        new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permission.WRITE_SYNC_SETTINGS; addPeriodicSync Account: " + param.args[0] + "; args1:" + param.args[1] + " stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });


    }
}
