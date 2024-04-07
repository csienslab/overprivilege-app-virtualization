package com.example.permlabel;

import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookLocationExtra_test implements IXposedHookLoadPackage {
    /*
    LocationManager: sendExtraCommand, getLastKnownLocation,
    TelephonyManager: getCellLocation, getAllCellInfo, getLine1Number,
    WifiManager: startScan, getConnectionInfo
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
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
        // Test LocationManager.sendExtraCommand() ; sample: com.klook
        XposedHelpers.findAndHookMethod("android.location.LocationManager", lpparam.classLoader, "sendExtraCommand", String.class, String.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_LOCATION_EXTRA_COMMANDS sendExtraCommand provider= " + param.args[0] + "; command=" + param.args[1] + "; extras=" + param.args[2] + "; res=" + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.location.LocationManager", lpparam.classLoader, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_FINE_LOCATION getLastKnownLocation res=" + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getCellLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.ACCESS_COARSE_LOCATION; getCellLocation res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getAllCellInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.ACCESS_COARSE_LOCATION; getAllCellInfo res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getNeighboringCellInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.ACCESS_COARSE_LOCATION; getNeighboringCellInfo res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getLine1Number", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permissin.READ_SMSo, android.permissin.READ_PHONE_NUMBERS; getLine1Number res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

    }
}
