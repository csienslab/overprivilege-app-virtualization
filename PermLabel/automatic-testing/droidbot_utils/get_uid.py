import os
import time
import subprocess
import sys
from androguard.misc import APK

dst_folder = "saved_apks/%s" % sys.argv[1]
if not os.path.exists(dst_folder):
    os.makedirs(dst_folder)

with open('testedpkglists.txt', 'r') as pkgList:
    pkgs = pkgList.readlines()

for i in pkgs:
    #print(i.strip())
    output = subprocess.getoutput("adb shell \"su -c 'cat /data/system/packages.xml | grep %s'\"" % i.strip())
    #print(output)
    grep_uid = output.split('userId="')[1]
    uid = grep_uid.split('" ')[0]
    print("uid != " + uid + " &&")
    
for i in pkgs:
    print("!lpparam.packageName.equals(\"" + i.strip() + "\") &&")
    import shutil
    
    src = "apks/%s.apk" % i.strip()
    dst = "%s/%s.apk" % (dst_folder, i.strip())

    shutil.copyfile(src, dst)
