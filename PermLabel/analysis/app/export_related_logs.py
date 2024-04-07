"""
This script is used to save permission-related logs to the post_analysis folder
We use "permission name" to identify the related logs.
Note that PermLabel hooks permission-related APIs and permission operations.
Input:
  - logcats/[app store]/[pkg]_logcat.txt
  - logcats_filtered/[app store]/[pkg]_logcat_filtered.txt
  - outputs/sources_classified_res/res_[app store].csv
Output:
  - post_analysis/[app store]/[pkg]/[perm].txt

"""

import sys
import os
import glob
import pandas as pd

post_dir = 'post_analysis'

if not os.path.exists(post_dir):
    os.makedirs(post_dir)


def parse(src, pkg, perms):

    logcat = 'logcats/%s/%s_logcat.txt' % (src, pkg)
    logcat_filtered = 'logcats_filtered/%s/%s_logcat_filtered.txt' % (src, pkg)

    if not os.path.exists(logcat):
        logcat = 'logcats_too_large/%s/%s_logcat.txt' % (src, pkg)
    if not os.path.exists(logcat_filtered):
        logcat_filtered = 'logcats_filrered_too_large/%s/%s_logcat_filtered.txt' % (src, pkg)

    chk_path = '%s/%s/%s' % (post_dir, src, pkg)
    if os.path.exists(chk_path):
        return

    with open(logcat_filtered, 'r', encoding='UTF8') as log_filtered:
        logs_flt = log_filtered.readlines()

        # Initialize perms_timestamp that saves the log timestamps for each permission
        perms_timestamp = dict()
        for perm in perms:
            perms_timestamp[perm] = []

        for line in logs_flt:
            for i in perms:
                if line.find(i) != -1:
                    timestp = line.split()[1]
                    perms_timestamp[i].append(timestp)

    with open(logcat, 'r', encoding='UTF8') as log:
        lines = log.readlines()

        perms_logs = dict()
        for perm in perms:
            perms_logs[perm] = []

        for line in lines:
            for i in perms_timestamp:
                # i is key (permission) in perms_timestamp
                # So we obtain the timestamp list of permission using perms_timestamp[i]
                for time in perms_timestamp[i]:
                    # Check if any timestamp element exists in the current iterated line
                    if time in line:
                        perms_logs[i].append(line)

    # Save the perms_logs to files
    for i in perms_logs:
        # If the list is not null
        if len(perms_logs[i]) > 0:
            pkg_dir = "%s/%s/%s" % (post_dir, src, pkg)
            if not os.path.exists(pkg_dir):
                os.makedirs(pkg_dir)

            fname = '%s/%s/%s/%s.txt' % (post_dir, src, pkg, i)
            with open(fname, 'w', encoding='UTF8') as f:
                f.write('\n'.join(perms_logs[i]))


if __name__ == '__main__':

    # If arguments do not exist, logs are exported for all samples.
    res_lst = glob.glob('outputs/sources_classified_res/*.csv')
    for res in res_lst:
        source = res.split('res_')[1].split('.csv')[0]
        src_dir = "%s/%s" % (post_dir, source)
        if not os.path.exists(src_dir):
            os.makedirs(src_dir)

        # Process csv file
        df = pd.read_csv(res)
        df = df.reset_index()  # make sure indexes pair with number of rows

        # Iterate through each pkgs
        for index, row in df.iterrows():

            package = row['Package Name']
            permissions = list()
            # print(type(row['(1) Install-time Labels.1']), row['(1) Runtime Labels.1'], row['(2) Runtime Labels.1'],
            # row['(3) Runtime Labels.1'])
            if type(row['(1) Install-time Labels.1']) == str:
                insert = [i.strip(" \'") for i in row['(1) Install-time Labels.1'].strip("['] ").split(',')]
                permissions.extend(insert)
            if type(row['(1) Runtime Labels.1']) == str:
                insert = [i.strip(" \'") for i in row['(1) Runtime Labels.1'].strip("['] ").split(',')]
                permissions.extend(insert)
            if type(row['(2) Runtime Labels.1']) == str:
                insert = [i.strip(" \'") for i in row['(2) Runtime Labels.1'].strip("['] ").split(',')]
                permissions.extend(insert)
            if type(row['(3) Runtime Labels.1']) == str:
                insert = [i.strip(" \'") for i in row['(3) Runtime Labels.1'].strip("['] ").split(',')]
                permissions.extend(insert)

            print(package, permissions)
            parse(source, package, permissions)

    # If arguments exist, we can specify which sample to extract post logs.
    src_arg = ''
    pkg_arg = ''
    perms_lst = list()

    if len(sys.argv) == 1:
        exit()
    elif 1 < len(sys.argv) < 4:
        print("Usage: python export_related_logs.py [src] [pkg] [permissions]")
        exit()
    else:
        src_arg = sys.argv[1].strip()
        pkg_arg = sys.argv[2].strip()

        src_dir = "%s/%s" % (post_dir, src_arg)
        if not os.path.exists(src_dir):
            os.makedirs(src_dir)

        for n in range(3, len(sys.argv)):
            perms_lst.append(sys.argv[n].strip())

        parse(src_arg, pkg_arg, perms_lst)
