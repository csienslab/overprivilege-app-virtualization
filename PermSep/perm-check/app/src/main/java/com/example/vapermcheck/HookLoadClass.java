package com.example.vapermcheck;

import static de.robv.android.xposed.XposedHelpers.findClass;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Parcelable;
import android.os.Process;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookLoadClass implements IXposedHookLoadPackage {

    List<String> component = new ArrayList<String>();
    List<String> pPkg = new ArrayList<String>();

    private boolean hasAdded = false;
    //int ct = 0;

    long newApplicationTime = 0;
    long getCompTime = 0;
    long setExtraTime = 0;
    long getCompTime_after = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        if (hasAdded) {
            return;
        }

        if (lpparam.packageName.startsWith(".system")
                || lpparam.packageName.startsWith("org.")
                || lpparam.packageName.startsWith("android.")
                || lpparam.packageName.contains("umeng.")
                || lpparam.packageName.contains("com.google")
                || lpparam.packageName.contains(".alipay")
                || lpparam.packageName.contains(".netease")
                || lpparam.packageName.contains(".alibaba")
                || lpparam.packageName.contains(".pgyersdk")
                || lpparam.packageName.contains(".daohen")
                || lpparam.packageName.contains(".bugly")
                || lpparam.packageName.contains("mini")
                || lpparam.packageName.contains("xposed")) {
            return;
        }

        Context systemContext = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(findClass("android.app.ActivityThread", lpparam.classLoader), "currentActivityThread"), "getSystemContext");

        // Filter out system packages
        PackageManager packageManager = systemContext.getPackageManager();
        try {
            if (packageManager != null){
                ApplicationInfo app = packageManager.getApplicationInfo(lpparam.packageName, 0);
                if (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                    return;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1) {
                if (Process.myUid() < 10000
                        || lpparam.packageName.equals("com.android.systemui")
                        || lpparam.packageName.equals("com.google.android.apps.wellbeing")
                        || lpparam.packageName.equals("com.google.android.gms.persistent")
                        || lpparam.packageName.equals("com.google.android.inputmethod.latin")) {
                    return;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        XposedHelpers.findAndHookMethod(
                "android.app.Instrumentation",
                lpparam.classLoader,
                "newApplication",
                ClassLoader.class,
                String.class,
                Context.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        if (lpparam.packageName.equals("com.ludashi.dualspace")) {
                            newApplicationTime = System.currentTimeMillis();
                        }
                    }

                    @SuppressLint("Range")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (hasAdded) {
                            return;
                        }

                        String pkg2 = String.valueOf(XposedHelpers.callMethod(param.getResult(), "getPackageName"));
                        if (pkg2.equals(lpparam.packageName)){
                            return;
                        }

                        // Find the context.getPackageCodePath()
                        String codePath = String.valueOf(XposedHelpers.callMethod(param.args[2], "getPackageCodePath"));

                        List<String> cutList = Arrays.asList(codePath.split("/"));
                        String pkg = Arrays.asList(cutList.get(3).split("-")).get(0);
                        Signature sig_pkg = systemContext.getPackageManager().getPackageInfo(pkg, PackageManager.GET_SIGNATURES).signatures[0];
                        byte[] md5_pkg = MessageDigest.getInstance("MD5").digest(sig_pkg.toByteArray());

                        Signature sig_apk = systemContext.getPackageManager().getPackageArchiveInfo(codePath, PackageManager.GET_SIGNATURES).signatures[0];
                        byte[] md5_apk = MessageDigest.getInstance("MD5").digest(sig_apk.toByteArray());

                        if (pkg2.equals("com.android.vending") || pkg2.equals("com.google.android.gms") || pkg.equals("GoogleServicesFramework")) {
                            if (Arrays.equals(md5_pkg, md5_apk)){
                                return;
                            }
                        }


                        Boolean isPlugin = false;
                        if (systemContext != null) {
                            ContentResolver contentResolver = systemContext.getContentResolver();
                            Uri CONTENT_URI = Uri.parse("content://" + VADatabase.AUTHORITY + VADatabase.PATH_CLASS);
                            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                if (cursor.moveToFirst()) {
                                    do {
                                        if (pkg2.equals(cursor.getString(cursor.getColumnIndex(VADatabase.Cls.PNAME)))){
                                            if (!pkg2.equals(lpparam.packageName)){
                                                isPlugin = true;
                                                break;
                                            }
                                        }
                                    } while (cursor.moveToNext());
                                }
                                cursor.close();
                            }
                        }

                        if (systemContext != null && isPlugin) {

                            final PackageManager pm = systemContext.getPackageManager();
                            String[] info = new String[2];
                            StringBuilder permissionsText = new StringBuilder();

                            PackageInfo pkgInfo = null;
                            try {
                                pkgInfo = pm.getPackageInfo(pkg2, PackageManager.GET_PERMISSIONS);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            ApplicationInfo appInfo = null;
                            try {
                                appInfo = pm.getApplicationInfo(pkg2, 0);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            if (appInfo != null) {
                                String appName = pm.getApplicationLabel(appInfo).toString();
                                String pkgName = appInfo.packageName;

                                info[0] = appName;
                                info[1] = pkgName;

                                ContentValues plugin = new ContentValues();
                                plugin.put(VADatabase.Plugin.NAME, info[1]);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1) {
                                    int uid = systemContext.getPackageManager().getApplicationInfo(info[1], 0).uid;
                                    plugin.put(VADatabase.Plugin.UID, uid);
                                }
                                plugin.put(VADatabase.Plugin.PID, Process.myPid());
                                Uri mUri = systemContext.getContentResolver().insert(VADatabase.URI_PLUGIN, plugin);

                                if (mUri != null) {
                                    hasAdded = true;
                                }
                            }

                        }


                        // This block is used for latency evaluation
                        //Log.e("latency_eval", "newApplication " + pkg2);
                        if (!pkg2.equals("com.example.permtest")){
                            return;
                        }
                        //Log.e("latency_eval", "newApplication " + pkg2);

                        long epochTime = System.currentTimeMillis();
                        //*Log.e("latency_eval", "newApplication " + String.valueOf(epochTime));
                        Log.e("latency_eval", "newApplication " + String.valueOf(epochTime - newApplicationTime));

                    }
                });

        XposedHelpers.findAndHookMethod(
                Intent.class,
                "getComponent",
                new XC_MethodHook() {
                    long getCompTime_before = 0;

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                        getCompTime_before = System.currentTimeMillis();
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

                        ComponentName inComp = (ComponentName) param.getResult();
                        if (inComp != null) {
                            component.add(inComp.flattenToString());
                        }

                        getCompTime_after = System.currentTimeMillis() - getCompTime_before;

                    }
                });

        XposedHelpers.findAndHookMethod(
                Intent.class,
                "setType",
                String.class,
                new XC_MethodHook() {
                    float setType_before = 0;
                    float setType_after = 0;

                    @SuppressLint("NewApi")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        setType_before = System.currentTimeMillis();
                        if (component.contains((String) param.args[0])) {
                            String pack = String.valueOf(param.args[0]);
                            insertHost(systemContext, pack);

                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        setType_after = System.currentTimeMillis();
                        Log.e("latency_eval", "getComponent " + String.valueOf(getCompTime_after));
                        Log.e("latency_eval", "setType " + String.valueOf(setType_after - setType_before));
                    }
                });

        XposedHelpers.findAndHookMethod(
                Intent.class,
                "putExtra",
                String.class,
                Parcelable.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        ComponentName comp;
                        String comp2 = "";
                        if (param.args[1] != null) {
                            if (param.args[1] instanceof Intent) {
                                comp = (ComponentName) XposedHelpers.callMethod(param.args[1], "getComponent");
                                if (comp != null){
                                    comp2 = (String) XposedHelpers.callMethod(comp, "flattenToString");
                                }

                            }
                        }

                        if (component.contains(comp2)) {
                            insertHost(systemContext, comp2);

                        }
                    }
                });
    }

    void insertHost(Context context, String plugin) {
        if (pPkg.contains(plugin)){
            return;
        }

        ContentValues cls = new ContentValues();
        cls.put(VADatabase.Cls.PNAME, plugin.split("/")[0]);
        context.getContentResolver().insert(VADatabase.URI_CLASS, cls);
        pPkg.add(plugin);
    }

}
