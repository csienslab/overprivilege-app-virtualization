package com.example.permlabel;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookAccount implements IXposedHookLoadPackage {
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

        XposedHelpers.findAndHookMethod("android.accounts.AccountManager", lpparam.classLoader, "getAccounts", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.GET_ACCOUNTS; getAccounts res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.accounts.AccountManager", lpparam.classLoader, "getAccountsByType", String.class,  new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.GET_ACCOUNTS; getAccountsByType res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });
    }
}
