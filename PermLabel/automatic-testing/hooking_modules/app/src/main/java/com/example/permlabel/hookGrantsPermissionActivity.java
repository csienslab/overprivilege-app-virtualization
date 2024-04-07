package com.example.permlabel;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class hookGrantsPermissionActivity implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals("com.google.android.packageinstaller")) {
            return;
        }

        XposedHelpers.findAndHookMethod(
                "com.android.packageinstaller.permission.ui.GrantPermissionsActivity",
                lpparam.classLoader,
                "onPermissionGrantResult",
                String.class,
                boolean.class,
                boolean.class,
                new XC_MethodHook() {

                        @SuppressLint("NewApi")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            XposedBridge.log("onPermissionGrantResult before perm: "+ param.args[0] + "; granted: " + param.args[1]+ "; doNotAskAgain: " + param.args[2]);
                            param.args[1] = true;
                            param.args[2] = false;
                            XposedBridge.log("onPermissionGrantResult after perm: "+ param.args[0] + "; granted: " + param.args[1]+ "; doNotAskAgain: " + param.args[2]);

                        }
                });
    }
}
