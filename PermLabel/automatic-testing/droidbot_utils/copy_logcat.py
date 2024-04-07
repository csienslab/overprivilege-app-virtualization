import glob
import shutil
import sys

'''
    This script copies the logcat.txt from each tested folder to logcats under analysis project
'''

if len(sys.argv) < 3:
    print("Usage: python copy_logcat.py [app store] [analysis project path]")
    exit()
else:
    source = sys.argv[1].strip()
    analysis = sys.argv[2].strip()
    if not os.path.exists(analysis + "/logcats"):
        os.makedirs(analysis + "/logcats")
    
with open('testedpkglists.txt', 'r') as pkgList:
    pkgs = pkgList.readlines()
    
for i in pkgs:
    pkg = i.strip()
    print(pkg)
    
    src = '%s/%s/logcat.txt' % (source, pkg)
    dst = '%s/logcats/%s/%s_logcat.txt' % (analysis, source, pkg)

    shutil.copyfile(src, dst)