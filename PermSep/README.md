# PermSep
The project includes two Android apps. 
- `content-provider` is used to implement the PermSep’s PID lookup table that saves the hooking data from its first two stages of
operation that are then used in the third stage.
- `perm-check` is a *LSPosed* hooking module that implements the core of permission checking.

## content-provider
Install content-provider on the Android device.

## perm-check
1. Install the [LSPosed Framework](https://github.com/LSPosed/LSPosed) on the Android device.
2. Install and enable the `perm-check` in the *LSPosed* app and choose the *System Framework* and *Package Installer* as hooking targets.
