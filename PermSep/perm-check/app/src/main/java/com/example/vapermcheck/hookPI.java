package com.example.vapermcheck;

import static de.robv.android.xposed.XposedHelpers.findClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.security.MessageDigest;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookPI implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.google.android.packageinstaller")) {
            return;
        }

        final String[] pkg = new String[1];

        Context systemContext = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(findClass("android.app.ActivityThread", lpparam.classLoader), "currentActivityThread"), "getSystemContext");

        XposedHelpers.findAndHookMethod(
                "com.android.packageinstaller.permission.ui.GrantPermissionsActivity",
                lpparam.classLoader,
                "getCallingPackageInfo",
                new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PackageInfo tmp = (PackageInfo) param.getResult();
                        String pkgName = tmp.packageName;

                        if (pkg[0] != null){
                            param.setResult(systemContext.getPackageManager().getPackageInfo(pkg[0], PackageManager.GET_PERMISSIONS));
                            long end = System.currentTimeMillis();
                            Log.e("latency_eval", "requestPermission end " + end);
                        }

                    }
                });


        XposedHelpers.findAndHookMethod(
                Activity.class,
                "getIntent",
                new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        pkg[0] = (String) XposedHelpers.callMethod(param.getResult(), "getExtra", "EXTRA_REQUEST_PACKAGE");
                    }
                });


    }
}
