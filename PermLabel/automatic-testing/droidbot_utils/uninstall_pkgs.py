import os
import sys

if len(sys.argv) < 2:
    print('Please input the file path containting the package lists to uninstall.')
    sys.exit()

path = sys.argv[1]
with open(path) as f:
    lst = f.readlines()
    lst = [i.strip() for i in lst]

for i in lst:
    os.system("adb uninstall " + i)
    #os.system("adb shell pm grant %s android.permission.ACCESS_FINE_LOCATION" % i)
    #os.system("adb shell pm grant %s android.permission.ACCESS_COARSE_LOCATION" % i)