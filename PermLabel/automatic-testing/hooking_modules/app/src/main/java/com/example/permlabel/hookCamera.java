package com.example.permlabel;

import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.util.Log;

import java.util.logging.Handler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookCamera implements IXposedHookLoadPackage  {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "getCameraInfo", int.class, Camera.CameraInfo.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  " android.permission.CAMERA getCameraInfo args0: " + param.args[0] + "; args1: " + param.args[1] + "; res= " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "open", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  " android.permission.CAMERA open res= " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                    }
                });

        XposedHelpers.findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "open", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  " android.permission.CAMERA open cameraId: " + param.args[0] + "; res= " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

                    }
                });

    }
}
