package com.example.vacontentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDatabaseManager extends SQLiteOpenHelper {


    public SqliteDatabaseManager(Context context) {
        super(context, VADatabase.DATABASE_NAME, null, VADatabase.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_PLUGIN_TABLE = "CREATE TABLE " + VADatabase.Plugin.TABLE_NAME
                + "(" + VADatabase.Plugin.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VADatabase.Plugin.NAME + " TEXT,"
                //+ VADatabase.Plugin.HPKG + " TEXT,"
                + VADatabase.Plugin.UID + " INT,"
                + VADatabase.Plugin.PID + " INT)";
                //+ VADatabase.Plugin.PERMISSIONS + " TEXT)";
                //+ "UNIQUE(" + VADatabase.Plugin.UID + "))";

        String CREATE_CLASS_TABLE = "CREATE TABLE " + VADatabase.Cls.TABLE_NAME
                + "(" + VADatabase.Cls.PNAME + " TEXT)";
               // + "UNIQUE(" + VADatabase.Plugin.UID + "))";

        // 創建表格
        sqLiteDatabase.execSQL(CREATE_PLUGIN_TABLE);
        sqLiteDatabase.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VADatabase.Plugin.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VADatabase.Cls.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

}

