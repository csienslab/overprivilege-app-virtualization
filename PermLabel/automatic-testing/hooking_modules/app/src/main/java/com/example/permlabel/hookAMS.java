package com.example.permlabel;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Binder;
import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookAMS implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android")) {
            final Class<?> activityManagerServiceClass = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", lpparam.classLoader);
            XposedBridge.hookAllMethods(activityManagerServiceClass, "startActivity", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    XposedHelpers.findAndHookMethod(
                            "com.android.server.am.ActivityManagerService",
                            lpparam.classLoader,
                            "checkPermission",
                            String.class, int.class, int.class, new XC_MethodHook() {

                                @SuppressLint("NewApi")
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) {
                                    String permName = (String) param.args[0];
                                    int pid = (int) param.args[1];
                                    int uid = (int) param.args[2];

                                    XposedBridge.log(pid + " PermLabel " + uid + " AMS.checkPermission: Binder.getCallingPid() = " + Binder.getCallingPid() + "; Binder.getCallingUid() = " + Binder.getCallingUid() + "myPid = " + android.os.Process.myPid() + "myUid = " + android.os.Process.myUid() + "; permName: " + permName + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                                }
                            });
                }
            });
        }
    }
}
