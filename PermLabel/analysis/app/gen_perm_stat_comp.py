"""
This code is used to generate overall over-privilege permission statistics for apps and malware.
Input:
  - outputs/abnormal_perms_add_request.json
  - malware/outputs/abnormal_perms_add_request.json
  - outputs/res_add_request.csv
  - malware/outputs/res_add_request.csv
Output:
  - outputs/final_statistics.csv
"""


import json
import csv
import pandas as pd
import os

normal_app = 'outputs/abnormal_perms_add_request.json'
malware = 'malware/outputs/abnormal_perms_add_request.json'
save = 'outputs/final_statistics.csv'

with open(normal_app) as d:
    normalData = json.load(d)
    normal_i1 = 0
    for v in normalData["Abnormal Install-time"]["1"]:
        if v == 'GET_TASKS':
            normal_i1_tasks = normalData["Abnormal Install-time"]["1"][v]
        else:
            normal_i1 += normalData["Abnormal Install-time"]["1"][v]
    # normal_i1 = sum([v for v in normalData["Abnormal Install-time"]["1"].values()])
    normal_r1 = sum([v for v in normalData["Abnormal Runtime"]["1"].values()])
    normal_r2 = sum([v for v in normalData["Abnormal Runtime"]["2"].values()])
    normal_r3 = sum([v for v in normalData["Abnormal Runtime"]["3"].values()])

with open(malware) as d:
    malData = json.load(d)
    mal_i1 = 0
    mal_i1_tasks = 0
    mal_r1 = 0
    mal_r2 = 0
    mal_r3 = 0
    if len(malData) != 0:
        for v in malData["API>=23"]["Abnormal Install-time"]["1"]:
            if v == 'GET_TASKS':
                mal_i1_tasks = malData["API>=23"]["Abnormal Install-time"]["1"][v]
            else:
                mal_i1 += malData["API>=23"]["Abnormal Install-time"]["1"][v]
        # mal_i1 = sum([v for v in malData["API>=23"]["Abnormal Install-time"]["1"].values()])
        mal_r1 = sum([v for v in malData["API>=23"]["Abnormal Runtime"]["1"].values()])
        mal_r2 = sum([v for v in malData["API>=23"]["Abnormal Runtime"]["2"].values()])
        mal_r3 = sum([v for v in malData["API>=23"]["Abnormal Runtime"]["3"].values()])

normal_app_csv = 'outputs/res_add_request.csv'
normal_data = pd.read_csv(normal_app_csv)
normal_df = pd.DataFrame(normal_data)
normal_tested_install = normal_df["Droidbot Install-time"].sum()
normal_tested_runtime = normal_df["Droidbot Runtime"].sum()

malware_csv = 'malware/outputs/res_add_request.csv'
if os.path.exists(malware_csv):
    malware_data = pd.read_csv(malware_csv)
    malware_df = pd.DataFrame(malware_data)
    malware_tested_install = malware_df["Droidbot Install-time"].sum()
    malware_tested_runtime = malware_df["Droidbot Runtime"].sum()
else:
    malware_tested_install = 0
    malware_tested_runtime = 0

with open(save, 'w', newline='', encoding='UTF8') as f:
    # create the csv writer
    writer = csv.writer(f)
    header = ['Abnormal Permissions', 'Normal Apps', 'Malware']
    writer.writerow(header)
    writer.writerow(['Install-time 1', str(normal_i1) + "/" + str(normal_tested_install) +
                     ', GET_TASKS: ' + str(normal_i1_tasks) + "/" + str(normal_tested_install),
                     str(mal_i1) + "/" + str(malware_tested_install) +
                     ', GET_TASKS: ' + str(mal_i1_tasks) + "/" + str(malware_tested_install)])
    writer.writerow(['Runtime 1', str(normal_r1) + "/" + str(normal_tested_runtime), str(mal_r1) + "/" + str(malware_tested_runtime)])
    writer.writerow(['Runtime 2', str(normal_r2) + "/" + str(normal_tested_runtime), str(mal_r2) + "/" + str(malware_tested_runtime)])
    writer.writerow(['Runtime 3', str(normal_r3) + "/" + str(normal_tested_runtime), str(mal_r3) + "/" + str(malware_tested_runtime)])