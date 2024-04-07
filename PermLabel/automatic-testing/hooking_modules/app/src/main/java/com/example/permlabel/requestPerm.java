package com.example.permlabel;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class requestPerm implements IXposedHookLoadPackage {
    List<String> askedperm = new ArrayList<>();
    List<String> loggedperm = new ArrayList<>();

    @SuppressLint("NewApi")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // Tested apps (if we do not add package filters, it will overwhelm.)
        if (!lpparam.packageName.equals("com.android.providers.media") &&
                !lpparam.packageName.equals("com.android.providers.downloads.ui") &&
                !lpparam.packageName.equals("com.android.mtp") &&
                !lpparam.packageName.equals("com.android.providers.downloads") &&
                !lpparam.packageName.equals("com.android.phone") &&
                !lpparam.packageName.equals("com.android.bluetooth") &&
                !lpparam.packageName.equals("com.binaryguilt.completemusicreadingtrainer") &&
                !lpparam.packageName.equals("com.economist.lamarr") &&
                !lpparam.packageName.equals("com.klook") &&
                !lpparam.packageName.equals("com.xiaomi.smarthome") &&
                !lpparam.packageName.equals("com.zhiliaoapp.musically.go")

        ) {
            /*
            !lpparam.packageName.equals("com.google.android.backuptransport") &&
                !lpparam.packageName.equals("com.google.android.gms") &&
                !lpparam.packageName.equals("com.google.android.gsf") &&
                !lpparam.packageName.equals("com.android.phone")
             */
            return;
        }

        // Identify the PID through package name
        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " package =" + lpparam.packageName + " " + android.os.Process.myPid());

        Context systemContext = (Context) XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader), "currentActivityThread"), "getSystemContext");

        // Get declared permissions
        PackageInfo pkgInfo = null;
        PackageManager packageManager = null;
        try {
            packageManager = systemContext.getPackageManager();
            pkgInfo = packageManager.getPackageInfo(lpparam.packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] requestedPermissions = pkgInfo.requestedPermissions;
        StringBuilder permissions = new StringBuilder();
        StringBuilder normal_permissions = new StringBuilder();
        StringBuilder dangerous_permissions = new StringBuilder();
        StringBuilder dangerous_permissions_group = new StringBuilder();

        if (requestedPermissions != null) {
            for (String requestedPermission : requestedPermissions) {
                permissions.append(requestedPermission).append(" ");
                int type;
                try {
                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(requestedPermission,  0);
                    if (android.os.Build.VERSION.SDK_INT >= 28) {
                        type = permissionInfo.getProtection();
                    } else {
                        type = permissionInfo.protectionLevel;
                    }
                    if (type == 0) {
                        normal_permissions.append(requestedPermission).append(" ");
                    } else if (type == 1) {
                        PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.group, 0);
                        String group = (String) permissionGroupInfo.loadLabel(packageManager);
                        dangerous_permissions.append(requestedPermission).append(" ");
                        dangerous_permissions_group.append(group).append(" ");
                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " permission_group_mapping =" + requestedPermission + " " + group);
                    } else {
                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " permission_type_mapping =" + requestedPermission + " " + type + "; ");
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        // Output the declared permissions to logs
        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " all_declared permissions =" + permissions.toString());
        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " declared normal_permissions =" + normal_permissions.toString());
        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " declared dangerous_permissions =" + dangerous_permissions.toString());
        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " declared dangerous_group =" + dangerous_permissions_group.toString());


        XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager",
                lpparam.classLoader,
                "checkPermission",
                String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String permName = (String) param.args[0];
                        String pkgName = (String) param.args[1];
                        int res = (int) param.getResult();

                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " PM.checkPermission permName:"  + permName + "; Package name: " + pkgName + "; Result: " + res + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                    }
                });

        // Self-check permissions
        XposedHelpers.findAndHookMethod(
                "android.app.ContextImpl",
                lpparam.classLoader,
                "checkPermission",
                String.class, int.class, int.class, new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String permName = (String) param.args[0];
                        int pid = (int) param.args[1];
                        int uid = (int) param.args[2];
                        int type;

                        /* && uid != 10114 10110 (superclone)
                        if (uid != 10122 && uid != 10111 &&
                                uid != 10112 && uid != 10113 &&
                                uid != 10114 && uid != 10115 &&
                                uid != 10116 && uid != 10117 &&
                                uid != 10118 && uid != 10119 &&
                                uid != 10120 ) {
                            return;
                        }

                         */

                        // Check the permission type
                        PackageManager packageManager = systemContext.getPackageManager();
                        try {
                            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permName,  0);
                            if (android.os.Build.VERSION.SDK_INT >= 28) {
                                type = permissionInfo.getProtection();
                            } else {
                                type = permissionInfo.protectionLevel;
                            }
                            if (type == 0 || type == 1){
                                if (permName.equals("android.permission.READ_PHONE_STATE") || permName.equals("android.permission.DOWNLOAD_WITHOUT_NOTIFICATION") || permName.equals("android.permission.RECORD_AUDIO")){
                                    int callingPid = Binder.getCallingPid();
                                    int callingUid = Binder.getCallingUid();
                                    XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " ContextImpl Binder.getCallingPid() = " + callingPid + "; Binder.getCallingUid() = " + callingUid + "; permName: " + permName + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                                }
                                if (!loggedperm.contains(permName)) {
                                    int callingPid = Binder.getCallingPid();
                                    int callingUid = Binder.getCallingUid();
                                    loggedperm.add(permName);
                                    XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " ContextImpl.checkPermission: askedperm = " + askedperm + "; permName: " + permName + "; android.os.Process.myPid() = " + android.os.Process.myPid() + "; type = " + type);
                                    XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " ContextImpl.checkPermission: pid = " + pid + "; uid = " + uid + "; Binder.getCallingPid() =" + callingPid + "; Binder.getCallingUid() = " + callingUid + "; permName: " + permName + "; type = " + type + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
                                }
                            } else {
                                return;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            return;
                        }

                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " ContextImpl.checkPermission: pid = " + pid + "; uid = " + uid + "; permName: " + permName + "; type = " + type + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                    }
                });

        // Request Permission
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "requestPermissions",
                String[].class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String[] permName = (String[]) param.args[0];
                        for (String perm: permName) {
                            askedperm.add(perm);
                            XposedBridge.log( android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " requestPermissions (Activity): pid = " + android.os.Process.myPid() + "; permName: "  + perm + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                        }
                    }
                });

        // Request Permission
        XposedHelpers.findAndHookMethod(
                "android.app.Fragment",
                lpparam.classLoader,
                "requestPermissions",
                String[].class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String[] permName = (String[]) param.args[0];
                        for (String perm: permName) {
                            askedperm.add(perm);
                            XposedBridge.log( android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + " startActivityAsUser requestPermissions (Fragment): pid = " + android.os.Process.myPid() + "; permName: "  + perm + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                        }

                    }
                });

    }
}