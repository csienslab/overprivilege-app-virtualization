"""
This script is executed after export_related_logs.py
Input:
  - post_analysis/libKeywords.csv
Output:
  - post_analysis/api_stack_flow.csv
  - post_analysis/api_stack.csv
  - post_analysis/api_stack_lib.csv
  - post_analysis/api_stack_lib_only.csv
"""

import csv
import glob
import json
import math
import pandas as pd


def check_(perm, api, perm_label, ct):
    time = line.split(" I")[0]

    n = 1
    caller = ''
    while ct + n < len(lines):
        if time not in lines[ct + n] or 'LSPosed-Bridge' not in lines[ct + n]:
            break
        if 'java.' not in lines[ct + n] and ' android.' not in lines[ct + n] and 'com.android.' not in lines[ct + n] and \
                'kotlinx.' not in lines[ct + n] and 'kotlin.' not in lines[ct + n] and 'permlabel' not in lines[ct + n] and \
                'LSPHooker_' not in lines[ct + n] and 'xposed' not in lines[ct + n] and 'androidx' not in lines[ct + n]:

            caller = lines[ct + n].split('at ')[-1].split('(')[0]
        n += 1

    # Determine the library and class from the information of "libKeywords.csv"
    lib_class = ''
    lib = ''
    for ct, value in enumerate(list(df['Keyword'])):
        if value in caller:
            lib_class = list(df.loc[ct])[1].strip()
            lib = list(df.loc[ct])[2].strip()
    if lib == '' and pkg in caller:
        lib = 'app'
        lib_class = 'app'

    return [perm, perm_label, src, pkg, api, caller, lib, lib_class]


save = 'post_analysis/api_stack.csv'
save_api_lib = 'post_analysis/api_stack_lib.csv'
save_lib = 'post_analysis/api_stack_lib_only.csv'
save_api = 'post_analysis/api_stack_api_only.csv'

logs = glob.glob("post_analysis/**/**/*.txt")
print(logs)
# logs = ['post_analysis\\wandoujia\\com.topgether.sixfoot\\CAMERA.txt']

save_flow = 'post_analysis/api_stack_flow.csv'

df = pd.read_csv("post_analysis/libKeywords.csv")

with open(save, 'w', newline='', encoding='utf-8-sig') as f, open(save_flow, 'w', newline='', encoding='utf-8-sig') as flow, \
        open(save_api_lib, 'w', newline='', encoding='utf-8-sig') as f_api_lib, open(save_lib, 'w', newline='', encoding='utf-8-sig') as f_lib, \
        open(save_api, 'w', newline='', encoding='utf-8-sig') as f_api:
    # create the csv writer
    writer = csv.writer(f)
    header = ['Abnormal Permissions', 'Label', 'Source', 'Package', 'API', 'Call stack', 'Library', 'Library Class']
    writer.writerow(header)

    writer_flow = csv.writer(flow)
    writer_flow.writerow(header)

    writer_lib = csv.writer(f_api_lib)
    writer_lib.writerow(header)

    writer_lib_only = csv.writer(f_lib)
    writer_lib_only.writerow(header)

    writer_api_only = csv.writer(f_api)
    writer_api_only.writerow(header)

    for i in logs:
        src = i.split('\\')[1]
        pkg = i.split('\\')[2]
        perm = i.split('\\')[3].split('.txt')[0]
        api = ''
        added_api = dict()
        added_api_lib = dict()
        added_lib = dict()
        # Used to record APIs that have been recorded
        added_api_only = dict()

        # Get the permission label
        label_path = "labels\\%s\\%s_label.json" % (src, pkg)
        with open(label_path) as d:
            #print(label_path)
            dictData = json.load(d)["Droidbot-obtained"]

            label = ''
            if perm == "com.google.android.gms.permission.ACTIVITY_RECOGNITION":
                perm_req = "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
            else:
                perm_req = "android.permission.%s" % perm
            normal_perm = {1:'IND'}
            dangerous_perm = {1:'RND', 2:'NR', 3:'RNDNR'}

            if 'filter_normal_self' in dictData:
                if perm_req in dictData["filter_normal_self"]:
                    label = normal_perm[dictData["filter_normal_self"][perm_req]]
            if 'add_dangerous_request' in dictData:
                if perm_req in dictData["add_dangerous_request"]:
                    label = dangerous_perm[dictData["add_dangerous_request"][perm_req]]

        # Read extracted logs under post_analysis
        # i is the log file for each permission
        with open(i, 'r', encoding='UTF8') as f:
            if 'GET_ACCOUNTS' in i:
                continue
            lines = f.read().splitlines()
            lines = [i for i in lines if i != '\n' and i != '']

            # Used to record all the caller of requesting permission, preventing the duplicated call stack.
            request = list()
            # Used to record all the library of requesting permission, preventing the duplicated library.
            request_lib = list()
            request_api = 0

            request_n = math.inf # Used to record the first permission requesting line number

            for line_n, line in enumerate(lines):
                # We only record the permission source that triggers permission-required APIs before request
                if line_n > request_n and (label == 'NR' or label == 'RNDNR'):
                    break

                if line == '\n':
                    continue
                line = line.strip("\n")

                if "requestPermissions (" in line:
                    # For RND permissions, requestPermissions is the API
                    if label == 'RND':
                        perm = line.split("permName: android.permission.")[1].split(';')[0]
                        api = 'requestPermissions'

                        add = check_(perm, api, label, line_n)

                        # Check duplication
                        if add[5] not in request:
                            writer.writerow(add)
                            request.append(add[5])
                        if add[6] not in request_lib:
                            writer_lib.writerow(add)
                            writer_lib_only.writerow(add)
                            request_lib.append(add[6])
                        if request_api == 0:
                            request_api = 1
                            writer_api_only.writerow(add)

                    # We only need to record the first requesting point
                    if request_n == math.inf:
                        request_n = line_n
                    '''
                    if add[5] not in request.values():
                        request[line_n] = add[5]
                    '''

                    continue

                if "AMS.checkPermission" in line:
                    # src, pkg, perm
                    # [perm, perm_label, src, pkg, api, caller, lib, lib_class]
                    writer_flow.writerow([perm, label, src, pkg, "AMS.checkPermission", "", "", ""])

                if "ServiceManager" in line or "PM.checkPermission" in line or "ContextImpl" in line or \
                        "AMS.checkPermission" in line or "declared" in line or "all_declared" in line or \
                        "permission_group_mapping" in line or "GET_TASKS" in line or "index" in line:
                    continue

                if 'READ_PHONE_STATE;' in line and 'READ_PHONE_STATE' in perm:
                    perm = 'READ_PHONE_STATE'
                    api = line.split("READ_PHONE_STATE; ")[1].split(' ')[0]
                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    # Eliminate duplicated (same API with same caller)
                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'CAMERA ' in line and 'CAMERA' in perm:
                    perm = 'CAMERA'
                    api = line.split("CAMERA ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if ' DOWNLOAD_WITHOUT_NOTIFICATION' in line and 'DOWNLOAD_WITHOUT_NOTIFICATION' in perm:
                    perm = 'DOWNLOAD_WITHOUT_NOTIFICATION'
                    api = line.split(" DOWNLOAD_WITHOUT_NOTIFICATION")[0].split(' ')[-1]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'ACCESS_LOCATION_EXTRA_COMMANDS ' in line and 'ACCESS_LOCATION_EXTRA_COMMANDS' in perm:
                    perm = 'ACCESS_LOCATION_EXTRA_COMMANDS'
                    api = line.split("ACCESS_LOCATION_EXTRA_COMMANDS ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'ACCESS_FINE_LOCATION ' in line and 'ACCESS_FINE_LOCATION' in perm:
                    perm = 'ACCESS_FINE_LOCATION'
                    api = line.split("ACCESS_FINE_LOCATION ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'ACCESS_COARSE_LOCATION;' in line and 'ACCESS_COARSE_LOCATION' in perm:
                    perm = 'ACCESS_COARSE_LOCATION'
                    api = line.split("ACCESS_COARSE_LOCATION; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'READ_PHONE_NUMBERS;' in line and 'READ_PHONE_NUMBERS' in perm:
                    perm = 'READ_PHONE_NUMBERS'
                    api = line.split("READ_PHONE_NUMBERS; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'READ_SMSo' in line and 'READ_SMS' in perm:
                    perm = 'READ_SMS'
                    api = line.split("android.permissin.READ_SMSo, android.permissin.READ_PHONE_NUMBERS; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'CHANGE_WIFI_STATE;' in line and 'CHANGE_WIFI_STATE' in perm:
                    perm = 'CHANGE_WIFI_STATE'
                    api = line.split("CHANGE_WIFI_STATE; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'ACCESS_WIFI_STATE;' in line and 'ACCESS_WIFI_STATE' in perm:
                    perm = 'ACCESS_WIFI_STATE'
                    api = line.split("ACCESS_WIFI_STATE; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'RESTART_PACKAGES; killBackgroundProcesses' in line and 'KILL_BACKGROUND_PROCESSES' in perm:
                    perm = 'KILL_BACKGROUND_PROCESSES'
                    api = line.split("RESTART_PACKAGES; ")[1].split(' ')[0]
                    #print(api)

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                # android.permission.KILL_BACKGROUND_PROCESSES, android.permission.RESTART_PACKAGES
                if 'KILL_BACKGROUND_PROCESSES, android.permission.RESTART_PACKAGES' in line and 'RESTART_PACKAGES' in perm:
                    perm = 'RESTART_PACKAGES'
                    api = line.split("RESTART_PACKAGES; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'USE_BIOMETRIC, android.permission.USE_FINGERPRINT; ' in line and 'USE_BIOMETRIC' in perm:
                    perm = 'USE_BIOMETRIC'
                    api = line.split("USE_FINGERPRINT; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'MODIFY_AUDIO_SETTINGS; ' in line and 'MODIFY_AUDIO_SETTINGS' in perm:
                    perm = 'MODIFY_AUDIO_SETTINGS'
                    api = line.split("MODIFY_AUDIO_SETTINGS; ")[1].split(' ')[0]
                    print(src, pkg, perm, api, added_api_only)

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'RECORD_AUDIO; ' in line and 'RECORD_AUDIO' in perm:
                    perm = 'RECORD_AUDIO'
                    api = line.split("RECORD_AUDIO; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'RECEIVE_BOOT_COMPLETED ' in line and 'RECEIVE_BOOT_COMPLETED' in perm:
                    perm = 'RECEIVE_BOOT_COMPLETED'
                    api = line.split("RECEIVE_BOOT_COMPLETED ")[1].split(' ')[0]
                    #print(api)

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'BLUETOOTH_ADMIN; ' in line and 'BLUETOOTH_ADMIN' in perm:
                    perm = 'BLUETOOTH_ADMIN'
                    api = line.split("BLUETOOTH_ADMIN; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        #print(perm in added_api_only)
                        #print(api in added_api_only[perm])
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

                if 'WRITE_SYNC_SETTINGS; ' in line and 'WRITE_SYNC_SETTINGS' in perm:
                    perm = 'WRITE_SYNC_SETTINGS'
                    api = line.split("WRITE_SYNC_SETTINGS; ")[1].split(' ')[0]

                    add = check_(perm, api, label, line_n)
                    writer_flow.writerow(add)

                    if api in added_api:
                        if add[5] in added_api[api]:
                            pass
                        else:
                            writer.writerow(add)
                            added_api[api].append(add[5])

                        if add[6] in added_api_lib[api]:
                            pass
                        else:
                            writer_lib.writerow(add)
                            added_api[api].append(add[6])
                    else:
                        writer.writerow(add)
                        writer_lib.writerow(add)
                        added_api[api] = [add[5]]
                        added_api_lib[api] = [add[6]]

                    if perm in added_lib:
                        if add[6] in added_lib[perm]:
                            pass
                        else:
                            writer_lib_only.writerow(add)
                            added_lib[perm].append(add[6])
                    else:
                        writer_lib_only.writerow(add)
                        added_lib[perm] = [add[6]]

                    if perm in added_api_only:
                        if api in added_api_only[perm]:
                            continue
                        else:
                            writer_api_only.writerow(add)
                            added_api_only[perm].append(api)
                    else:
                        writer_api_only.writerow(add)
                        added_api_only[perm] = [api]

            if api == '' and perm != "GET_TASKS":
                writer.writerow([perm, label, src, pkg, '', ''])
                writer_lib.writerow([perm, label, src, pkg, '', ''])
                writer_api_only.writerow([perm, label, src, pkg, '', ''])
                writer_lib_only.writerow([perm, label, src, pkg, '', ''])
