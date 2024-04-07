# PermLabel
## Workflow 
<img src="https://github.com/csienslab/overprivilege-app-virtualization/blob/main/materials/imgs/PermLabel%20workflow.png"  width="40%" height="20%">

## File Structure

```
├─automatic-testing
│   ├─droidbot_utils            (Scripts used with Droidbot)
│   │   └─script_samples        
│   └─hooking_modules           (LSPosed modules installed at Android device)
│       └─app
└─analysis                      (Generating the results of PermLabel)
   ├─app
   │  ├─labels                  (Detailed labeling of permissions for each tested apps)
   │  ├─logcats_filtered
   │  ├─outputs                 (Results of overprivileged permissions)
   │  └─post_analysis           (Statistics of API and library usage through call stacks)
   └─malware
      ├─labels
      ├─logcats_filtered
      ├─outputs
      ├─utils
      └─versions                (Versions of malware sampels)
```

## How to use

### hooking_modules
1. Install the [LSPosed Framework](https://github.com/LSPosed/LSPosed) on the Android device.
2. Compile and install the `hooking_modules`.
3. Install the tested apps from app stores.
4. Enable the `hooking_modules` in the LSPosed app and choose *System Framework* and the tested apps as the hooking targets.

### droidbot_utils
1. Clone the [DroidBot](https://github.com/honeynet/droidbot).
2. Place our droidbot utils at the root of DroidBot's project.
3. Add the tested apps' package names in the testedpkglists.txt, divided by line breaks.
4. Run
    ```cmd
    python obtain_apk.py
    python execute_droidbot.py [app store]
    python copy_logcat.py [app store] [analysis project path]
    ```

### analysis
1. Generate permission labels
   `python parse_local_all.py`
2. Generate over-privileged statistics
   `python generate_res.py`
3. Generate overall over-privilege permission statistics for apps and malware
   `python gen_perm_stat_comp.py`
4. Save permission-related logs to the post_analysis folder
   `python export_related_logs.py`
5. Identify the permission usage (caller and the API)
   `python extract_api_stack.py`

