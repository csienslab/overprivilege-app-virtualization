import os
import time

with open('host_apps', 'r') as pkgList: # testedpkglists.txt
    pkgs = pkgList.readlines()

for i in pkgs:
    print(i.strip())
    install_cmd = "adb shell am start -a android.intent.action.VIEW -d 'market://details?id=%s'" % i.strip()
    os.system(install_cmd)
    time.sleep(5)
