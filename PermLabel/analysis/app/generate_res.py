"""
This code is used to parse the label outputs from parse_logcat_all.py, and generates explainable outputs.
Input:
  - labels/[app store]/[pkg]_label.json
Output:
  - outputs/res_[policy].csv
  - outputs/res_request_detail.csv
  - outputs/sources_classified_res/abnormal_perms_[app store].json
  - outputs/sources_classified_res/res_[app store].csv
"""


import json
import glob
import csv
import sys
import os


def process_labels(policy):
    label_global = dict()
    labels_lst = glob.glob("app/labels/**/*.json", recursive=True)

    if policy == 'filter_normal':
        n_tag = "filter_normal_self"
        d_tag = "Dangerous"
    elif policy == 'filter_normal_strict':
        n_tag = "filter_normal_self_strict"
        d_tag = "Dangerous"
    elif policy == 'add_request':
        # We adopt this policy in the paper
        n_tag = "filter_normal_self"
        d_tag = "add_dangerous_request"
    else:
        n_tag = "Normal"
        d_tag = "add_dangerous_request"

    source = ""
    for label in labels_lst:
        source = label.split("\\")[1].split("\\")[0]
        pkg = label.split("\\")[2].split("_label")[0]

        with open(label) as d:
            dictData = json.load(d)
            if source not in label_global:
                label_global[source] = dict()
            if pkg not in label_global[source]:
                # If no pkg labels in the global label_all dict, initialize the pkg dict first.
                label_global[source][pkg] = dict()
                label_global[source][pkg]["Declared"] = dict()
                label_global[source][pkg]["Droidbot-obtained"] = dict()
                label_global[source][pkg]["Declared"]["Normal"] = dictData["Declared"]["Normal"]
                label_global[source][pkg]["Declared"]["Dangerous"] = dictData["Declared"]["Dangerous"]
                label_global[source][pkg]["Droidbot-obtained"]["Normal"] = {0: [], 1: []}
                label_global[source][pkg]["Droidbot-obtained"]["Dangerous"] = {0: [], 1: [], 2: [], 3: []}
            for perm in dictData["Droidbot-obtained"][n_tag]:
                if 'GET_ACCOUNTS' in perm:
                    continue

                # Iterate through all the permission items in the current label.json
                cur_label = dictData["Droidbot-obtained"][n_tag][perm]
                for label, label_lst in label_global[source][pkg]["Droidbot-obtained"]["Normal"].items():
                    if perm in label_lst:
                        # If we can find the perm already in one of the label_lst
                        # Compare the current label with the previous label, and retain the larger one
                        if cur_label > label:
                            label_global[source][pkg]["Droidbot-obtained"]["Normal"][label].remove(perm)
                            label_global[source][pkg]["Droidbot-obtained"]["Normal"][cur_label].append(perm)
                            print('different label', perm, label, cur_label)
                        # Because one perm only maps to one label for the current label.json
                        # If the perm is already mapped to one list and processed,
                        # we use break to prevent reaching the else block for the for loop
                        break
                else:
                    # While iterating through all the global data and not found the already existed label,
                    # add the current label to the global data
                    label_global[source][pkg]["Droidbot-obtained"]["Normal"][cur_label].append(perm)

            for perm in dictData["Droidbot-obtained"][d_tag]:
                if 'GET_ACCOUNTS' in perm:
                    # This permission has already deprecated.
                    continue

                cur_label = dictData["Droidbot-obtained"][d_tag][perm]
                for label, label_lst in label_global[source][pkg]["Droidbot-obtained"]["Dangerous"].items():
                    if perm in label_lst:
                        if cur_label > label:
                            label_global[source][pkg]["Droidbot-obtained"]["Dangerous"][label].remove(perm)
                            label_global[source][pkg]["Droidbot-obtained"]["Dangerous"][cur_label].append(perm)
                            print('different label', pkg, perm, label, cur_label)
                        break
                else:
                    label_global[source][pkg]["Droidbot-obtained"]["Dangerous"][cur_label].append(perm)
    return label_global


def find_ver(pkg):
    with open('apks.json') as v:
        version_dic = json.load(v)
    if pkg in version_dic:
        min_v, tar_v = version_dic[pkg]
    else:
        min_v, tar_v = -1, -1
    return min_v, tar_v


def writetocsv(label_all, save, s_opt=''):
    perm_cal = dict()
    perm_cal["Abnormal Install-time"] = dict()
    perm_cal["Abnormal Install-time"][1] = dict()
    perm_cal["Abnormal Runtime"] = dict()
    perm_cal["Abnormal Runtime"][1] = dict()
    perm_cal["Abnormal Runtime"][2] = dict()
    perm_cal["Abnormal Runtime"][3] = dict()
    with open(save, 'w', newline='', encoding='UTF8') as f:
        # create the csv writer
        writer = csv.writer(f)
        header = ['Package Name', 'Source', 'Declared Install-time', 'Declared Runtime',
                  'Droidbot Install-time', 'Droidbot Runtime', '(0) Install-time Normal', '(1) IND',
                  '(0) Runtime Normal', '(1) RND', '(2) NR', '(3) RNDNR',
                  '(1) IND Permissions',
                  '(1) RND Permissions', '(2) NR Permissions', '(3) RNDNR Permissions', 'MinVersion', 'TargetVersion']
        writer.writerow(header)

        # Find the source of packages
        # with open("./app_source.csv", 'r') as file:
        #    reader = csv.DictReader(file)
        #    result = {}
        #    for row in reader:
        #        #print(row)
        #        result[row['package']] = row['source']

        # write rows to the csv file
        for app_store in label_all:
            for pkg in label_all[app_store]:
                print(pkg)
                if s_opt != '' and app_store != s_opt:
                    continue
                declared_normal = label_all[app_store][pkg]["Declared"]["Normal"][0]
                declared_dangerous = label_all[app_store][pkg]["Declared"]["Dangerous"][0]
                droid_normal = sum([len(i) for i in label_all[app_store][pkg]["Droidbot-obtained"]["Normal"].values()])
                droid_dangerous = sum([len(i) for i in label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"].values()])
                normal_0 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Normal"][0])
                normal_1 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Normal"][1])
                normal_1_lst = [i.split('.')[-1] if i.startswith('android.permission') else i for i in label_all[app_store][pkg]["Droidbot-obtained"]["Normal"][1]] if normal_1 > 0 else ''
                dangerous_0 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][0])
                dangerous_1 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][1])
                dangerous_1_lst = [i.split('.')[-1] if i.startswith('android.permission') else i for i in label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][1]] if dangerous_1 > 0 else ''
                dangerous_2 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][2])
                dangerous_2_lst = [i.split('.')[-1] if i.startswith('android.permission') else i for i in label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][2]] if dangerous_2 > 0 else ''
                dangerous_3 = len(label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][3])
                dangerous_3_lst = [i.split('.')[-1] if i.startswith('android.permission') else i for i in label_all[app_store][pkg]["Droidbot-obtained"]["Dangerous"][3]] if dangerous_3 > 0 else ''
                min_ver, target_ver = find_ver(pkg)
                row = [pkg, app_store, declared_normal, declared_dangerous, droid_normal, droid_dangerous, normal_0, normal_1, dangerous_0, dangerous_1, dangerous_2, dangerous_3, normal_1_lst, dangerous_1_lst, dangerous_2_lst, dangerous_3_lst, min_ver, target_ver]
                writer.writerow(row)

                # The following code is to generate the statistics of abnormal permissions
                # to the output file abnormal_perms_[policy].json
                # abnormal_perm_runtime = list(dangerous_1_lst) + list(dangerous_2_lst) + list(dangerous_3_lst)
                for ab_perm in normal_1_lst:
                    if ab_perm in perm_cal["Abnormal Install-time"][1]:
                        perm_cal["Abnormal Install-time"][1][ab_perm] += 1
                    else:
                        perm_cal["Abnormal Install-time"][1][ab_perm] = 1
                for ab_perm in dangerous_1_lst:
                    if ab_perm in perm_cal["Abnormal Runtime"][1]:
                        perm_cal["Abnormal Runtime"][1][ab_perm] += 1
                    else:
                        perm_cal["Abnormal Runtime"][1][ab_perm] = 1
                for ab_perm in dangerous_2_lst:
                    if ab_perm in perm_cal["Abnormal Runtime"][2]:
                        perm_cal["Abnormal Runtime"][2][ab_perm] += 1
                    else:
                        perm_cal["Abnormal Runtime"][2][ab_perm] = 1
                for ab_perm in dangerous_3_lst:
                    if ab_perm in perm_cal["Abnormal Runtime"][3]:
                        perm_cal["Abnormal Runtime"][3][ab_perm] += 1
                    else:
                        perm_cal["Abnormal Runtime"][3][ab_perm] = 1
    return perm_cal


if __name__ == '__main__':

    if len(sys.argv) < 2:
        policy = 'all'
        policies = ["default", "filter_normal", "add_request"]
        for pol in policies:
            labels_global = process_labels(pol)
            save_csv_path = "outputs/res_%s.csv" % pol
            save_json_path = "outputs/abnormal_perms_%s.json" % pol
            perm_cals = writetocsv(labels_global, save_csv_path, '')
            with open(save_json_path, 'w') as saved_ab_perms:
                json.dump(perm_cals, saved_ab_perms, indent=4)
    else:
        # policy: default, filter_normal, filter_normal_strict, add_request
        policy = sys.argv[1]
        labels_global = process_labels(policy)
        save_csv_path = "outputs/res_%s.csv" % policy
        save_json_path = "outputs/abnormal_perms_%s.json" % policy
        perm_cals = writetocsv(labels_global, save_csv_path)
        with open(save_json_path, 'w') as saved_ab_perms:
            json.dump(perm_cals, saved_ab_perms, indent=4)

    sources = os.listdir("app/labels/")

    labels_global = process_labels("add_request")
    perm_cals_dict = dict()
    for source in sources:
        save_csv_path = "outputs/sources_classified_res/res_%s.csv" % source
        save_json_path = "outputs/sources_classified_res/abnormal_perms_%s.json" % source
        perm_cals = writetocsv(labels_global, save_csv_path, source)
        with open(save_json_path, 'w') as saved_ab_perms:
            json.dump(perm_cals, saved_ab_perms, indent=4)
        perm_cals_dict[source] = perm_cals

    # Generate statistics of each over-privileged permission found from apps from different stores
    detail = "outputs/res_add_request_detail.csv"
    with open(detail, 'w', newline='', encoding='UTF8') as saved_details:
        writer = csv.writer(saved_details)
        header = ['']
        source_lst = list(perm_cals_dict.keys())
        header.extend(source_lst)
        writer.writerow(header)

        all_install = list()
        all_req_1 = list()
        all_req_2 = list()
        all_req_3 = list()

        for i in perm_cals_dict:
            print("i", i)
            if 1 in perm_cals_dict[i]["Abnormal Install-time"]:
                all_install.extend(list(perm_cals_dict[i]["Abnormal Install-time"][1].keys()))
            if 1 in perm_cals_dict[i]["Abnormal Runtime"]:
                all_req_1.extend(list(perm_cals_dict[i]["Abnormal Runtime"][1].keys()))
            if 2 in perm_cals_dict[i]["Abnormal Runtime"]:
                all_req_2.extend(list(perm_cals_dict[i]["Abnormal Runtime"][2].keys()))
            if 3 in perm_cals_dict[i]["Abnormal Runtime"]:
                all_req_3.extend(list(perm_cals_dict[i]["Abnormal Runtime"][3].keys()))

        all_install = list(set(all_install))
        try:
            all_install.remove('GET_TASKS')
        except ValueError:
            pass
        all_req_1 = list(set(all_req_1))
        all_req_2 = list(set(all_req_2))
        all_req_3 = list(set(all_req_3))
        print(all_install)
        print(all_req_1)
        print(all_req_2)
        print(all_req_3)

        writer.writerow(["Abnormal Install-time"])

        for i in all_install:
            insrt = [i]
            for src in source_lst:
                src_i = perm_cals_dict[src]["Abnormal Install-time"][1][i] if i in perm_cals_dict[src]["Abnormal Install-time"][1] else 0
                insrt.append(src_i)
            writer.writerow(insrt)

        writer.writerow(["Abnormal Runtime 1"])
        for i in all_req_1:
            insrt = [i]
            for src in source_lst:
                src_i = perm_cals_dict[src]["Abnormal Runtime"][1][i] if i in perm_cals_dict[src]["Abnormal Runtime"][1] else 0
                insrt.append(src_i)
            writer.writerow(insrt)

        writer.writerow(["Abnormal Runtime 2"])
        for i in all_req_2:
            insrt = [i]
            for src in source_lst:
                src_i = perm_cals_dict[src]["Abnormal Runtime"][2][i] if i in perm_cals_dict[src]["Abnormal Runtime"][2] else 0
                insrt.append(src_i)
            writer.writerow(insrt)

        writer.writerow(["Abnormal Runtime 3"])
        for i in all_req_3:
            insrt = [i]
            for src in source_lst:
                src_i = perm_cals_dict[src]["Abnormal Runtime"][3][i] if i in perm_cals_dict[src]["Abnormal Runtime"][3] else 0
                insrt.append(src_i)
            writer.writerow(insrt)








