package com.example.permlabel;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class requirePerm implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        Class<?> IApplicationThread = Class.forName("android.app.IApplicationThread");
        Class<?> IBinder = Class.forName("android.os.IBinder");
        Class<?> ProfilerInfo = Class.forName("android.app.ProfilerInfo");

        Class<?> activityThread = Class.forName("android.app.ActivityThread");

        XposedBridge.hookAllMethods(activityThread, "systemMain", new XC_MethodHook() {

            protected void afterHookedMethod(MethodHookParam param) {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                Context systemContext = (Context) XposedHelpers.callMethod(
                        XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", classLoader), "currentActivityThread"), "getSystemContext");
                XposedBridge.log("initial systemContext: " + systemContext.getPackageName());

                Class<?> ams = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", classLoader);

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
                        //int.class,
                        new XC_MethodHook() {

                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                int pid = Binder.getCallingPid();
                                int uid = Binder.getCallingUid();
                                Intent intent = (Intent) param.args[2];

                                XposedBridge.log(pid + " PermLabel " + uid + " startActivityAsUser: Binder.getCallingPid() = " + pid + "; Binder.getCallingUid() = " + uid + "; intent = " + intent + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                                Bundle bundle = intent.getExtras();
                                if (bundle != null) {
                                    for (String key : bundle.keySet()) {
                                        XposedBridge.log(pid + " PermLabel " + uid + " startActivityAsUser: intent = " + intent);
                                        if (key.equals("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES")){
                                            String[] mRequestedPermissions = (String[]) XposedHelpers.callMethod(intent, "getStringArrayExtra", key);
                                            for (String perm : mRequestedPermissions){
                                                XposedBridge.log(pid + " PermLabel " + uid + " startActivityAsUser permName: " + (perm != null ? perm: "NULL"));
                                            }
                                        }

                                    }
                                }
                            }
                        });

                XposedHelpers.findAndHookMethod(
                        ams,
                        "checkPermission",
                        String.class, int.class, int.class, new XC_MethodHook() {

                            @SuppressLint("NewApi")
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                String permName = (String) param.args[0];
                                int pid = (int) param.args[1];
                                int uid = (int) param.args[2];
                                int type;

                                if (uid != 10125 &&
                                        uid != 10127 &&
                                        uid != 10128 &&
                                        uid != 10130 &&
                                        uid != 10131) {
                                    return;
                                }

                                // Check the permission type
                                PackageManager packageManager = systemContext.getPackageManager();
                                try {
                                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(permName,  0);
                                    if (android.os.Build.VERSION.SDK_INT >= 28) {
                                        type = permissionInfo.getProtection();
                                    } else {
                                        type = permissionInfo.protectionLevel;
                                    }
                                    if (type == 0){
                                        String callingApp = packageManager.getNameForUid(Binder.getCallingUid());
                                        String[] callingPkg = packageManager.getPackagesForUid(Binder.getCallingUid());
                                        XposedBridge.log(pid + " PermLabel " + uid + " AMS.checkPermission: Binder.getCallingPid() = " + Binder.getCallingPid() + "; uid = " + uid + "; Binder.getCallingUid() = " + Binder.getCallingUid() + "; getNameForUid: " + callingApp + "; getPackagesForUid: " + Arrays.toString(callingPkg) + "; permName: " + permName + "; type = " + type + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                                    } else if (type == 1) {
                                        if (Binder.getCallingUid() != uid) {
                                            String callingApp = packageManager.getNameForUid(Binder.getCallingUid());
                                            String[] callingPkg = packageManager.getPackagesForUid(Binder.getCallingUid());
                                            XposedBridge.log(pid + " PermLabel " + uid + " AMS.checkPermission: Binder.getCallingPid() = " + Binder.getCallingPid() + "; uid = " + uid + "; Binder.getCallingUid() = " + Binder.getCallingUid() + "; getNameForUid: " + callingApp + "; getPackagesForUid: " + Arrays.toString(callingPkg) + "; permName: " + permName + "; type = " + type + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                                            //XposedBridge.log(pid + " PermLabel " + uid + " AMS.checkPermission: Binder.getCallingPid() = " + Binder.getCallingPid() + "; uid = " + uid + "; Binder.getCallingUid() = " + Binder.getCallingUid() + "; getNameForUid: " + callingApp + "; getPackagesForUid: " + Arrays.toString(callingPkg) + "; uid: " + uid + "; permName: " + permName + "; type = " + type + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                                        }
                                    }

                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

            }
        });
    }
}

