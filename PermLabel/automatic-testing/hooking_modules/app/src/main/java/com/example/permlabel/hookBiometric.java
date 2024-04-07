package com.example.permlabel;

import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.os.CancellationSignal;

import java.util.concurrent.Executor;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookBiometric implements IXposedHookLoadPackage {

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

        /*
        public void authenticate (CancellationSignal cancel,
                Executor executor,
                BiometricPrompt.AuthenticationCallback callback)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            XposedHelpers.findAndHookMethod("android.hardware.biometrics.BiometricPrompt", lpparam.classLoader, "authenticate", CancellationSignal.class, Executor.class, BiometricPrompt.AuthenticationCallback.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permissin.USE_BIOMETRIC, android.permission.USE_FINGERPRINT; authenticate stacktrace=" + Log.getStackTraceString(new Exception()));
                }
            });

            XposedHelpers.findAndHookMethod("android.hardware.biometrics.BiometricPrompt", lpparam.classLoader, "authenticate", BiometricPrompt.CryptoObject.class, CancellationSignal.class, Executor.class, BiometricPrompt.AuthenticationCallback.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permissin.USE_BIOMETRIC, android.permission.USE_FINGERPRINT; authenticate stacktrace=" + Log.getStackTraceString(new Exception()));
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XposedHelpers.findAndHookMethod("android.hardware.fingerprint.FingerprintManager", lpparam.classLoader, "authenticate", FingerprintManager.CryptoObject.class, CancellationSignal.class, int.class, FingerprintManager.AuthenticationCallback.class, Handler.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permissin.USE_BIOMETRIC, android.permission.USE_FINGERPRINT; authenticate stacktrace=" + Log.getStackTraceString(new Exception()));
                }
            });
        }

        XposedHelpers.findAndHookMethod("android.hardware.fingerprint.FingerprintManager", lpparam.classLoader, "hasEnrolledFingerprints", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permission.USE_FINGERPRINT; hasEnrolledFingerprints res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.hardware.fingerprint.FingerprintManager", lpparam.classLoader, "isHardwareDetected", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(Process.myPid() + " PermLabel " + Process.myUid() + "; permName: android.permission.USE_FINGERPRINT; isHardwareDetected res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });


    }
}
