package com.example.permlabel;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.telecom.PhoneAccountHandle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.util.List;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookBinder_test implements IXposedHookLoadPackage {
    @RequiresApi(api = Build.VERSION_CODES.M)
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

        XposedHelpers.findAndHookMethod("android.os.BinderProxy", lpparam.classLoader, "transact", int.class, Parcel.class, Parcel.class, int.class, new XC_MethodHook() {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                IBinder binder = (IBinder) param.thisObject;
                int arg0 = (int) param.args[0];
                Parcel arg1 = (Parcel) param.args[1];
                Parcel arg2 = (Parcel) param.args[2];
                int arg3 = (int) param.args[3];

                String interfaceBinder = binder.getInterfaceDescriptor();
                XposedBridge.log(android.os.Process.myPid() + " PermLabel hookBinder_test " + android.os.Process.myUid() +  " interfaceBinder =" + interfaceBinder + "; arg0 = " + arg0 + "; arg1 = " + arg1 + "; arg2 = " + arg2 + "; arg3 = " + arg3 + "; res = " + param.getResult()+ "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.app.ActivityManager", lpparam.classLoader, "getRunningAppProcesses", new XC_MethodHook() {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                List<ActivityManager.RunningAppProcessInfo> res = (List) param.getResult();
                for (ActivityManager.RunningAppProcessInfo temp : res) {

                    XposedBridge.log(android.os.Process.myPid() + " PermLabel getRunningAppProcesses =" + temp);
                    int pid = temp.pid; // pid
                    String processName = temp.processName; // process name
                    int importance = temp.importance;
                    XposedBridge.log(android.os.Process.myPid() + " PermLabel getRunningAppProcesses processName: " + processName + " pid: " + pid);
                    XposedBridge.log(android.os.Process.myPid() + " PermLabel getRunningAppProcesses importance: " + importance + " pid: " + pid);

                    String[] pkgNameList = temp.pkgList; // all app package names

                    // Iterate over each package name
                    for (int i = 0; i < pkgNameList.length; i++) {
                        String pkgName = pkgNameList[i];
                        XposedBridge.log(android.os.Process.myPid() + " PermLabel getRunningAppProcesses packageName " + pkgName + " at index " + i+ " in process " + pid);
                    }
                }

                XposedBridge.log(android.os.Process.myPid() + " PermLabel permName: android.permission.READ_PHONE_STATE; getRunningAppProcesses =" + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));

            }
        });

        XposedHelpers.findAndHookMethod("android.app.ActivityManager", lpparam.classLoader, "killBackgroundProcesses", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.KILL_BACKGROUND_PROCESSES, android.permission.RESTART_PACKAGES; killBackgroundProcesses args0: " + param.args[0] + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.app.ActivityManager", lpparam.classLoader, "restartPackage", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.KILL_BACKGROUND_PROCESSES, android.permission.RESTART_PACKAGES; restartPackage args0: " + param.args[0] + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionManager", lpparam.classLoader, "getActiveSubscriptionInfo", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getActiveSubscriptionInfo args0: " + param.args[0] + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getSubscriberId", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getSubscriberId res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getSubscriptionId", PhoneAccountHandle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getSubscriptionId res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getDeviceId args0: " + param.args[0] + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getDeviceId", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getDeviceId res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getImei", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getImei args0: " + param.args[0] + "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getImei", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getImei res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getIccAuthentication", int.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getIccAuthentication appType: " + param.args[0] + "; authType = " + param.args[1] + "; data = " + param.args[2]+ "; res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getSimSerialNumber", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getSimSerialNumber res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getServiceState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getServiceState res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getVisualVoicemailPackageName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getVisualVoicemailPackageName res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getVoiceMailAlphaTag", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getVoiceMailAlphaTag res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getVoiceMailNumber", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getVoiceMailNumber res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getVoiceNetworkType", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getVoiceNetworkType res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader, "getCarrierConfig", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                XposedBridge.log(android.os.Process.myPid() + " PermLabel " + android.os.Process.myUid() +  "; permName: android.permission.READ_PHONE_STATE; getCarrierConfig res = " + param.getResult() + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookConstructor("android.media.AudioRecord", lpparam.classLoader, int.class, int.class, int.class, int.class, int.class, new XC_MethodHook() {
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(android.os.Process.myPid() + " PermLabel AudioRecord arg0 = " + param.args[0] + "; arg1 = " + param.args[1] + "; arg2 = " + param.args[2] + "; arg3 = " + param.args[3] + "; arg4 = " + param.args[4]+ "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "startActivityForResult", String.class, Intent.class, int.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param){
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                Intent intent = (Intent) param.args[1];

                XposedBridge.log(pid + " PermLabel " + uid + " startActivityAsUser: Binder.getCallingPid() = " + pid + "; Binder.getCallingUid() = " + uid + "; intent = " + intent + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod("com.android.phone", lpparam.classLoader, "getImeiForSlot", int.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                String callingPackage = (String) param.args[1];

                XposedBridge.log(pid + " PermLabel " + uid +  "; permName: android.permission.READ_PHONE_STATE; getImeiForSlot: Binder.getCallingPid() = " + pid + "; Binder.getCallingUid() = " + uid + "; permName: android.permission.READ_PHONE_STATE; callingPackage = " + callingPackage + "; stacktrace=" + Log.getStackTraceString(new Exception()));
            }
        });

    }
}
