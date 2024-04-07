package com.example.vapermcheck;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.ResolveInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PermCheckHook implements IXposedHookZygoteInit {

    public static String getAppNameByPID(Context context, int pid){
        ActivityManager manager
                = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()){
            if(processInfo.pid == pid){
                return processInfo.processName;
            }
        }
        return "";
    }

    public static String getpkgListByPID(Context context, int pid){
        ActivityManager manager
                = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()){
            if(processInfo.pid == pid){
                return processInfo.pkgList[0];
            }
        }
        return "";
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        final Context[] aContext = {null};

        Class<?> IApplicationThread = Class.forName("android.app.IApplicationThread");
        Class<?> IBinder = Class.forName("android.os.IBinder");
        Class<?> ProfilerInfo = Class.forName("android.app.ProfilerInfo");
        Class<?> activityThread = Class.forName("android.app.ActivityThread");

        final int[] n = {0};

        XposedBridge.hookAllMethods(activityThread, "systemMain", new XC_MethodHook() {

            @SuppressLint("PrivateApi")
            protected void afterHookedMethod(MethodHookParam param) throws ClassNotFoundException {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                HashMap checkMap = new HashMap();
                List<String> hosts = new ArrayList<String>();

                Context systemContext = (Context) XposedHelpers.callMethod(
                        XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", classLoader), "currentActivityThread"), "getSystemContext");
                XposedBridge.log("initial systemContext: " + systemContext.getPackageName());

                Class<?> ams = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", classLoader);
                Class<?> packageManagerService = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", classLoader);

                XposedHelpers.findAndHookConstructor(ams,
                        Context.class,
                        new XC_MethodHook() {

                            protected void afterHookedMethod(MethodHookParam param) {
                                Context context = (Context) param.args[0];
                                aContext[0] = context;
                                XposedBridge.log("findAndHookConstructor: " + param.args[0].toString());

                            }
                        });


                XposedHelpers.findAndHookMethod(
                        ams,
                        "checkPermission",
                        String.class, int.class, int.class, new XC_MethodHook() {

                            @SuppressLint("NewApi")
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws PackageManager.NameNotFoundException {

                                int pid = (int) param.args[1];
                                String permName = (String) param.args[0];
                                int uid = (int) param.args[2];

                                String[] packages;
                                PackageManager packageManager = systemContext.getPackageManager();

                                if (pid == -1) {
                                    return;
                                }

                                try {
                                    packages = packageManager.getPackagesForUid(uid);
                                    if (packages == null) {
                                        return;
                                    }
                                    ApplicationInfo app = packageManager.getApplicationInfo(packages[0], 0);
                                    if (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                                        return;
                                    }
                                    if (uid < 10000 || param.args[0] == null || packages[0].equals("com.android.systemui") || packages[0].equals("com.google.android.apps.wellbeing") || packages[0].equals("com.google.android.gms.persistent") || packages[0].equals("com.google.android.inputmethod.latin")) {
                                        return;
                                    }
                                    if (permName.equals("android.permission.REAL_GET_TASKS") || permName.equals("android.permission.GET_TASKS")) {
                                        return;
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                                // Check the permission type
                                int type;
                                try {
                                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(permName,  0);
                                    if (android.os.Build.VERSION.SDK_INT >= 28) {
                                        type = permissionInfo.getProtection();
                                    } else {
                                        type = permissionInfo.protectionLevel;
                                    }

                                    if (type!=0 && type!=1 && type!=2){
                                        return;
                                    }

                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                String pkg = null;

                                if (checkMap.containsKey(pid)) {
                                    List lst = (List) checkMap.get(pid);

                                    uid = systemContext.getPackageManager().getApplicationInfo((String) lst.get(0), 0).uid;
                                    param.args[2] = uid;
                                }
                            }

                            @SuppressLint("NewApi")
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                String permName = (String) param.args[0];

                                if (!permName.equals("ACCESS_FINE_LOCATION")){
                                    return;
                                }
                                long epochTime = System.currentTimeMillis();
                                Log.e("latency_eval", "AMS.checkPermission " + String.valueOf(epochTime));
                            }
                        });

                XposedHelpers.findAndHookMethod(ams, "startActivity",
                        IApplicationThread,
                        String.class,
                        Intent.class,
                        String.class,
                        IBinder,
                        String.class,
                        int.class,
                        int.class,
                        ProfilerInfo,
                        Bundle.class,
                        new XC_MethodHook() {

                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                Bundle extras = ((Intent) param.args[2]).getExtras();
                                Set<String> bundleKeySet = extras.keySet(); // string key set
                                for(String key : bundleKeySet){ // traverse and print pairs
                                    XposedBridge.log("hookAMS " + key + " : " + extras.get(key));
                                }

                                int pid = Binder.getCallingPid();

                                /*
                                This part is used to address the startActivity
                                 */

                                Intent intent_ = (Intent) param.args[2];


                                if (intent_.toString().contains("com.example.custompermtest/com.example.custompermtest.NormalActivity")) {
                                    XposedBridge.log("hookAMS startActivityAsUser in: " + intent_.getType());
                                    intent_.setComponent(ComponentName.unflattenFromString(intent_.getType()));
                                    XposedBridge.log("hookAMS startActivityAsUser comp: " + intent_.getComponent());
                                }
                                if (intent_.toString().contains("com.example.custompermtest/com.example.custompermtest.DangerousActivity")) {
                                    XposedBridge.log("hookAMS startActivityAsUser in: " + intent_.getType());
                                    intent_.setComponent(ComponentName.unflattenFromString(intent_.getType()));
                                    XposedBridge.log("hookAMS startActivityAsUser comp: " + intent_.getComponent());
                                }


                                if (checkMap.containsKey(pid)) {
                                    List lst = (List) checkMap.get(pid);
                                    String pkg = (String) lst.get(0);
                                    Intent intent = (Intent) param.args[2];
                                    intent.putExtra("EXTRA_REQUEST_PACKAGE", pkg);
                                }
                            }
                        });

                XposedHelpers.findAndHookMethod(ams, "checkComponentPermission", String.class, int.class, int.class, int.class, boolean.class, new XC_MethodHook() {
                    int a = 0;

                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @SuppressLint("Range")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        int uid = (int) param.args[2];
                        String permName = (String) param.args[0];

                        String[] packages;
                        PackageManager packageManager = systemContext.getPackageManager();

                        try {
                            packages = packageManager.getPackagesForUid(uid);
                            if (packages == null) {
                                return;
                            }
                            ApplicationInfo app = packageManager.getApplicationInfo(packages[0], 0);
                            if (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                                return;
                            }
                            if (uid < 10000 || param.args[0] == null || packages[0].equals("com.android.systemui") || packages[0].equals("com.google.android.apps.wellbeing") || packages[0].equals("com.google.android.gms.persistent") || packages[0].equals("com.google.android.inputmethod.latin")) {
                                return;
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }


                        if (a == 0) {
                            HandlerThread thread = new HandlerThread("MyHandlerThread");
                            thread.start();
                            // creates the handler using the passed looper
                            Handler handler = new Handler(thread.getLooper());

                            VAContentObserver aVAContentObserver = new VAContentObserver(aContext[0], handler, checkMap);

                            Uri CONTENT_URI = VADatabase.URI_PLUGIN;
                            ContentResolver contentResolver = aContext[0].getContentResolver();
                            if (contentResolver != null && aVAContentObserver != null) {
                                contentResolver.registerContentObserver(CONTENT_URI, true, aVAContentObserver);
                            }
                            a = 1;

                        }

                        // This part of code is used for testing custom permission
                        int type;
                        try {
                            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permName,  0);
                            if (android.os.Build.VERSION.SDK_INT >= 28) {
                                type = permissionInfo.getProtection();
                            } else {
                                type = permissionInfo.protectionLevel;
                            }

                            if (type==0 || type==1 || type==2){
                                XposedBridge.log("hookAMS checkComponentPermission: Binder.getCallingPid() = " + Binder.getCallingPid() + "; Permission = " + param.args[0] + "; uid = " + uid + "; pid = " + param.args[1] + "; owningUid = " + param.args[3] + "; res =  " + param.getResult());;
                            }

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                });


            }
        });
    }

}