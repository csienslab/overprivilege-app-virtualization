package com.example.vacontentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class VADatabase {
    public static final String AUTHORITY = "com.example.vacontentprovider";
    public static final String PATH_HOST = "/host";
    public static final String PATH_PLUGIN = "/plugin";
    public static final String PATH_CLASS = "/cls";
    public static final String PATH_STATUS = "/status";
    public static final Uri URI_HOST = Uri.parse("content://" + AUTHORITY + PATH_HOST);
    public static final Uri URI_PLUGIN = Uri.parse("content://" + AUTHORITY + PATH_PLUGIN);
    public static final Uri URI_CLASS = Uri.parse("content://" + AUTHORITY + PATH_CLASS);
    public static final Uri URI_STATUS = Uri.parse("content://" + AUTHORITY + PATH_STATUS);


    public static final String CONTENT_PHONEBOOK_LIST = "vnd.android.cursor.dir/vnd.com.yang.mycontentprovider";
    public static final String CONTENT_PHONEBOOK_ITEM = "vnd.android.cursor.item/vnd.com.yang.mycontentprovider";

    // 資料庫名稱
    public static final String DATABASE_NAME = "VADatabase";

    // 資料庫版本號碼
    public static final int DATABASE_VERSION = 486;

    public static class Cls implements BaseColumns {

        private Cls(){}

        // 表名稱
        public static final String TABLE_NAME = "cls";

        public static final String ID = "_id";
        //public static final String HNAME = "host_package";
        public static final String PNAME = "plugin_package";
        //public static final String CNAME = "plugin_component";
        //public static final String SIGNATURE = "sig";
        //public static final String HOST_PKG = "host_package";

    }

    public static class Plugin implements BaseColumns {

        private Plugin(){}

        // 表名稱
        public static final String TABLE_NAME = "plugin";

        public static final String ID = "_id";
        public static final String NAME = "name";
        //public static final String HPKG = "hpkg";
        public static final String UID = "uid";
        public static final String PID = "pid";
        //public static final String PERMISSIONS = "permissions";

    }

}


