package com.example.permlabel;

import android.app.PendingIntent;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookBluetooth implements IXposedHookLoadPackage {
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

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "stopScan", ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; stopScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "stopScan", PendingIntent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; stopScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, PendingIntent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, PendingIntent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_COARSE_LOCATION; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_COARSE_LOCATION; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, PendingIntent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_FINE_LOCATION startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", List.class, ScanSettings.class, ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_FINE_LOCATION startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_COARSE_LOCATION; startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.le.BluetoothLeScanner", lpparam.classLoader, "startScan", ScanCallback.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_FINE_LOCATION startScan res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "cancelDiscovery", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; cancelDiscovery res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "startDiscovery", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; startDiscovery res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "startDiscovery", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.ACCESS_FINE_LOCATION startDiscovery res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "disable", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; disable res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "enable", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; enable res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "setName", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH_ADMIN; setName res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getAddress res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getBondedDevices", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getBondedDevices res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getName res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getName res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getProfileConnectionState", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getProfileConnectionState res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getScanMode", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getScanMode res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "getState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; getState res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isDiscovering", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isDiscovering res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isEnabled res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isLe2MPhySupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isLe2MPhySupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isLeCodedPhySupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isLeCodedPhySupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isLeExtendedAdvertisingSupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isLeExtendedAdvertisingSupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isLePeriodicAdvertisingSupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isLePeriodicAdvertisingSupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isMultipleAdvertisementSupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isMultipleAdvertisementSupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isOffloadedFilteringSupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isOffloadedFilteringSupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isOffloadedScanBatchingSupported", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; isOffloadedScanBatchingSupported res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "listenUsingInsecureRfcommWithServiceRecord", String.class, UUID.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; listenUsingInsecureRfcommWithServiceRecord res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "listenUsingRfcommWithServiceRecord", String.class, UUID.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.BLUETOOTH; listenUsingRfcommWithServiceRecord res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });
    }
}
