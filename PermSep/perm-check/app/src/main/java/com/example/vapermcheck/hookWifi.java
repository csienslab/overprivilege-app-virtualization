package com.example.vapermcheck;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.util.Log;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookWifi implements IXposedHookLoadPackage {
    public static final int[] ct = {0};

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.contains("android")) {
            return;
        }

        XposedHelpers.findAndHookMethod(
                "com.android.server.wifi.WifiServiceImpl",
                lpparam.classLoader,
                "startScan",
                String.class,
                new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        int pid = (int) Binder.getCallingPid();

                        if (VAContentObserver.mcheckMap.containsKey(pid)) {
                            ct[0] = pid;
                        }
                    }

                    @SuppressLint("NewApi")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Log.e("latency_eval", "startScan WifiServiceImpl res = " + param.getResult());
                    }

                });

        XposedHelpers.findAndHookMethod(
                "com.android.server.wifi.util.WifiPermissionsUtil",
                lpparam.classLoader,
                "enforceCanAccessScanResults",
                String.class, int.class, new XC_MethodHook() {

                    @SuppressLint("NewApi")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        int uid = (int) param.args[1];
                        String pkgName = (String) param.args[0];

                        if (uid < 10000 || pkgName == null || ct[0] == 0) {
                            return;
                        }

                        if (VAContentObserver.mcheckMap.containsKey(ct[0])) {
                            Application context = AndroidAppHelper.currentApplication();
                            int pid = ct[0];
                            List lst = (List) VAContentObserver.mcheckMap.get(pid);

                            pkgName = (String) lst.get(0);
                            uid = Integer.parseInt((String) lst.get(1));

                            param.args[1] = uid;
                            param.args[0] = pkgName;
                            ct[0] = 0;
                        }

                    }

                });


    }
}
