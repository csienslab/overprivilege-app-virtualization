package com.example.permlabel;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

// This hooking module is used to trace the source triggering DOWNLOAD_WITHOUT_NOTIFICATION permission
public class hookDownload_test implements IXposedHookLoadPackage {
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

        // Trace the
        // 2022-08-04 21:36:08.892 6441-6491/? I/LSPosed-Bridge: 6441 PermLabel 10026 ContextImpl.checkPermission: pid = 6524; uid = 10133; permName: android.permission.DOWNLOAD_WITHOUT_NOTIFICATION; type = 0; stacktrace=java.lang.Exception
        //        at com.example.permlabel.requestPerm$2.afterHookedMethod(requestPerm.java:345)
        //        at de.robv.android.xposed.LspHooker.handleHookedMethod(Unknown Source:136)
        //        at LspHooker_.checkPermission(Unknown Source:25)
        //        at android.app.ContextImpl.checkCallingOrSelfPermission(ContextImpl.java:1791)
        //        at android.content.ContextWrapper.checkCallingOrSelfPermission(ContextWrapper.java:754)
        //        at com.android.providers.downloads.DownloadProvider.checkInsertPermissions(DownloadProvider.java:861)
        //        at com.android.providers.downloads.DownloadProvider.insert(DownloadProvider.java:571)
        //        at android.content.ContentProvider$Transport.insert(ContentProvider.java:266)
        //        at android.content.ContentProviderNative.onTransact(ContentProviderNative.java:152)
        //        at android.os.Binder.execTransact(Binder.java:731)
        /*
        XposedHelpers.findAndHookMethod(
                "com.android.providers.downloads.DownloadProvider",
                lpparam.classLoader,
                "insert",
                Uri.class, ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Uri uri = (Uri) param.args[0];
                        ContentValues values = (ContentValues) param.args[1];
                        XposedBridge.log( android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " insert DOWNLOAD_WITHOUT_NOTIFICATION uri = " + uri + "; values: "  + values+ "; Binder.getCallingUid(): "  + Binder.getCallingUid() + "; res=" + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                    }
                });

         */

        XposedHelpers.findAndHookMethod(
                "android.app.DownloadManager",
                lpparam.classLoader,
                "enqueue",
                DownloadManager.Request.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        DownloadManager.Request request = (DownloadManager.Request) param.args[0];

                        XposedBridge.log( android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " enqueue DOWNLOAD_WITHOUT_NOTIFICATION Request = " + param.args[0].toString() + "; Binder.getCallingUid(): "  + Binder.getCallingUid() + "; res=" + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                    }
                });

        XposedHelpers.findAndHookConstructor("android.app.DownloadManager.Request", lpparam.classLoader, Uri.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel DOWNLOAD_WITHOUT_NOTIFICATION Request = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.app.DownloadManager", lpparam.classLoader, "request", Uri.class, new XC_MethodHook(){
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel DOWNLOAD_WITHOUT_NOTIFICATION Request = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookConstructor("android.app.DownloadManager.Query", lpparam.classLoader, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel DOWNLOAD_WITHOUT_NOTIFICATION Query = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });


    }
}
