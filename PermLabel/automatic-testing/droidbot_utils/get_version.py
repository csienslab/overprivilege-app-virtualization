import os.path
import sys
import glob
import json
import csv
import pandas as pd
from pyaxmlparser import APK
import time


def get_apk_size(path_to_apk):
    file_size = os.stat(path_to_apk).st_size / (1024 * 1024) # MB
    return file_size


def get_info(path_to_apk):
    #a, d, dx = AnalyzeAPK(path_to_apk)
    try:
        apk = APK(path_to_apk)
        min_ver = apk.get_min_sdk_version()
        target_ver = apk.get_target_sdk_version()
        app_ver = apk.version_name
        app_ver_code = apk.version_code
        app_name = apk.application
        #declared_perm = len(apk.get_declared_permissions())
        #get_app_name = apk.get_app_name()
        public_v3 = 0
        if apk.signed_v2 and len(apk.get_public_keys_der_v3()) > 0:
            public_v3 = apk.get_public_keys_der_v3()[0].hex()
        public_v2 = 0
        if apk.signed_v2 and len(apk.get_public_keys_der_v2()) > 0:
            public_v2 = apk.get_public_keys_der_v2()[0].hex()

    except:
        print("Bad apk file: ", path_to_apk)
        min_ver = target_ver = 0
        app_ver = 0
        app_ver_code = 0
        public_v3 = 0
        public_v2 = 0
        app_name = 0
        get_app_name = 0
        #declared_perm = 0

    return min_ver, target_ver, app_ver, app_ver_code, public_v3, public_v2, app_name


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('Please input path of folder containing samples.')
        sys.exit()

    folderName = sys.argv[1]
    save_path = folderName + '_info.csv'

    if os.path.exists(save_path):
        os.remove(save_path)
        time.sleep(2)

    if not os.path.exists(save_path):
        with open(save_path, 'w', newline='', encoding='utf-8-sig') as f:
            writer = csv.writer(f)

            header = ['package', 'app_name', 'source', 'size', 'app_version_name',
                      'app_version_code', 'public_key_v3', 'public_key_v2', 'min_sdk_version', 'target_sdk_version']
            writer.writerow(header)

    df = pd.read_csv(save_path)

    with open(save_path, 'a', newline='', encoding='utf-8-sig') as f:
        writer = csv.writer(f)
    
        path_samples = glob.glob(folderName+'/**/*.apk')
        for i in path_samples:
            source = i.split('\\')[1]
            pkg = i.split('\\')[2].split('.apk')[0]

            existed_sources = df[df['package'].str.contains(pkg)]['source'].to_string(index=False).strip()

            if source in existed_sources:
                continue

            print(i)
            min_v, tar_v, app_v, app_v_c, pub_v3, pub_v2, app_n = get_info(i)
            size = get_apk_size(i)
            print(min_v, tar_v, app_v, app_v_c)

            writer.writerow([pkg, app_n, source, size, app_v, app_v_c, pub_v3, pub_v2, min_v, tar_v])