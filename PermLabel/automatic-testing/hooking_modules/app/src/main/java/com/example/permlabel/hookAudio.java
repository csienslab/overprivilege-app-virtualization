package com.example.permlabel;

import android.app.PendingIntent;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.media.MediaSyncEvent;
import android.speech.RecognitionListener;
import android.util.Log;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookAudio implements IXposedHookLoadPackage {
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

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setMode", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setMode mode = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setStreamMute", int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setStreamMute streamType = " + param.args[0] + "; state=" + param.args[1] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setStreamVolume", int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setStreamVolume streamType = " + param.args[0] + "; index=" + param.args[1] + "; flags=" + param.args[2] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setRingerMode", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setRingerMode mode = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setSpeakerphoneOn", boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setSpeakerphoneOn on = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "setMicrophoneMute", boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.MODIFY_AUDIO_SETTINGS; setMicrophoneMute on = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.speech.SpeechRecognizer", lpparam.classLoader, "startListening", Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; startListening stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.speech.SpeechRecognizer", lpparam.classLoader, "stopListening", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; stopListening stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.speech.SpeechRecognizer", lpparam.classLoader, "triggerModelDownload", Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; triggerModelDownload stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.speech.SpeechRecognizer", lpparam.classLoader, "setRecognitionListener", RecognitionListener.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; setRecognitionListener stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.speech.SpeechRecognizer", lpparam.classLoader, "isRecognitionAvailable", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; isRecognitionAvailable res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioRecord", lpparam.classLoader, "startRecording", MediaSyncEvent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; startRecording syncEvent = " + param.args[0] + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.media.AudioRecord", lpparam.classLoader, "startRecording", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() + "; permName: android.permission.RECORD_AUDIO; startRecording; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

    }
}
