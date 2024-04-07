import os

if not os.path.exists("apks"):
    os.makedirs(directory)

output_stream = os.popen("adb shell su -c 'ls -al /data/app/'")
lists = output_stream.read()
lists_lst = list(lists.split("\n"))


for i in lists_lst:
    if '==' in i:
        pkg = i.strip().split()[-1].split("-")[0]
        folder = i.strip().split()[-1]
        print(pkg)

        cmd = "adb pull /data/app/" + folder + "/base.apk apks/" + pkg + ".apk"
        os.popen(cmd)
