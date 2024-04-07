package com.example.vapermcheck;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookContextTest implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        /*
        if (!lpparam.packageName.equals("com.ludashi.dualspace") &&
                !lpparam.packageName.equals("com.excelliance.multiaccounts") &&
                !lpparam.packageName.equals("com.example.permtest")){
            return;
        }

         */

        XposedHelpers.findAndHookMethod(
                "android.app.ContextImpl",
                lpparam.classLoader,
                "checkPermission",
                String.class, int.class, int.class, new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int pid = (int) param.args[1];
                        //Log.e("latency_eval", "ContextImpl.checkPermission: pid = " + pid + "; param.args[0]:" + param.args[0] + "; Binder.getCallingPid() = " + Binder.getCallingPid());

                        if (Binder.getCallingPid() != pid && pid == android.os.Process.myPid()){
                            //Log.e("latency_eval", "in ContextImpl.checkPermission: pid = " + pid + "; param.args[0]:" + param.args[0] + "; Binder.getCallingPid() = " + Binder.getCallingPid());
                            param.args[1] = Binder.getCallingPid();
                        }
                    }

                });


        XposedHelpers.findAndHookMethod(
                "android.app.ContextImpl", ClassLoader.getSystemClassLoader(),
                "startActivity",
                Intent.class, Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[0];

                        XposedBridge.log("hookAMS ContextImpl : Binder.getCallingPid() = " + Binder.getCallingPid() + "; Intent = " + intent);
                    }
                });

        XposedHelpers.findAndHookMethod(
                "android.app.ContextImpl", ClassLoader.getSystemClassLoader(),
                "startActivity",
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[0];

                        XposedBridge.log("hookAMS ContextImpl : Binder.getCallingPid() = " + Binder.getCallingPid() + "; Intent = " + intent);
                    }
                });

        XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager", lpparam.classLoader,
                "checkPermission",
                String.class, String.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String permName = (String) param.args[0];
                        int res = (int) param.getResult();

                        if (Binder.getCallingPid() != android.os.Process.myPid()){
                            //Log.e("latency_eval", " after ApplicationPackageManager.checkPermission android.os.Process.myPid():" + android.os.Process.myPid() + "; Binder.getCallingPid() = " + Binder.getCallingPid() + "; package: " + param.args[1] + "; Permission name: "  + permName + "; res: " + res);
                            Application context = AndroidAppHelper.currentApplication();
                            int ams_res = (int) XposedHelpers.callMethod(context, "checkPermission", permName, Binder.getCallingPid(), android.os.Process.myUid());
                            param.setResult(ams_res);
                            //Log.e("latency_eval", context.toString());
                            //Log.e("latency_eval", "after ApplicationPackageManager.checkPermission android.os.Process.myPid():" + android.os.Process.myPid() + "; Binder.getCallingPid() = " + Binder.getCallingPid() + "; package: " + param.args[1] + "; Permission name: "  + permName + "; res: " + ams_res);

                        }
                    }
                });


    }
}
