import subprocess
import os
import time
import sys

res_dir = 'outputs/'

if len(sys.argv) <= 1:
    print("Usage: python execute_droidbot.py [app store]")
    exit()
else:
    source = sys.argv[1]
    directory = "%s/%s" % (res_dir, sys.argv[1])
    if not os.path.exists(directory):
        os.makedirs(directory)

with open('testedpkglists.txt', 'r') as pkgList:
    pkgs = pkgList.readlines()

for pkg in pkgs:
    pkgName = pkg.strip()

    policy = "dfs_greedy"
    droidbot_cmd = "python ../start.py -a apks/%s.apk -o %s/%s/%s -policy %s -script grant_permission_script.json" % (pkgName, curr_dir, source, pkgName, policy)
    print(droidbot_cmd)
    p = subprocess.Popen(droidbot_cmd, shell=True, creationflags=subprocess.CREATE_NEW_PROCESS_GROUP)

    # Test for 30 minutes
    time.sleep(1800)
    subprocess.call(['taskkill', '/F', '/T', '/PID', str(p.pid)])
    os.system("adb uninstall io.github.ylimit.droidbotapp")

    uninstall_cmd = "adb uninstall %s" % pkgName
    os.system(uninstall_cmd)
    time.sleep(2)

    os.system("adb shell reboot")
    time.sleep(40)
    os.system("adb shell input swipe 200 500 200 0")
    # This 10 sec is to let the device connect to Wifi.
    time.sleep(10)



