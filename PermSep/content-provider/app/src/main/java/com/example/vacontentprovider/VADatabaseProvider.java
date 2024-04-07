package com.example.vacontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VADatabaseProvider extends ContentProvider {

    private SqliteDatabaseManager dbManager;
    private SQLiteDatabase db;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(VADatabase.AUTHORITY, VADatabase.PATH_PLUGIN, 1);
        mUriMatcher.addURI(VADatabase.AUTHORITY, VADatabase.PATH_CLASS, 2);

        mUriMatcher.addURI(VADatabase.AUTHORITY, VADatabase.PATH_PLUGIN + "/#", 3);
        mUriMatcher.addURI(VADatabase.AUTHORITY, VADatabase.PATH_CLASS + "/#", 4);

        // 若URI資源路徑 = content://com.yang.mycontentprovider/memberbook ，則返回註冊碼 1
        // 若URI資源路徑 = content://com.yang.mycontentprovider/memberbook/數字 ，則返回註冊碼 2
    }


    // 以下是ContentProvider的6个方法

    /**
     * 初始化ContentProvider
     */
    @Override
    public boolean onCreate() {
        dbManager = new SqliteDatabaseManager(getContext());
        db = dbManager.getWritableDatabase();
        return false;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        //SQLiteDatabase db = dbManager.getWritableDatabase();
        Cursor mCursor;

        switch (mUriMatcher.match(uri)) {
            case 1:
                mCursor = db.query(VADatabase.Plugin.TABLE_NAME, projections, selection, selectionArgs, null, null, null);
                break;
            case 2:
                mCursor = db.query(VADatabase.Cls.TABLE_NAME, projections, selection, selectionArgs, null, null, null);
                break;
            case 3:
                selection = VADatabase.Plugin.ID + " = " + uri.getLastPathSegment();
                mCursor = db.query(VADatabase.Plugin.TABLE_NAME, projections, selection, selectionArgs, null, null, null);
                break;
            case 4:
                selection = VADatabase.Cls.ID + " = " + uri.getLastPathSegment();
                mCursor = db.query(VADatabase.Cls.TABLE_NAME, projections, selection, selectionArgs, null, null, null);
                break;
            default:
                Toast.makeText(getContext(), "Invalid content uri", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        /*if (mUriMatcher.match(uri) != 1) {
            Log.e("VAContentProvider: ", "mUriMatcher.match(uri) != 1");
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }*/
        switch (mUriMatcher.match(uri)) {
            case 1:
                long rowId_p = db.insert(VADatabase.Plugin.TABLE_NAME, null, contentValues);
                if (rowId_p > 0) {
                    Uri uriPlugin = ContentUris.withAppendedId(VADatabase.URI_PLUGIN, rowId_p);
                    getContext().getContentResolver().notifyChange(uriPlugin, null);
                    //getContext().getContentResolver().notifyChange(uriPlugin, null);
                    return uriPlugin;
                }
                break;
            case 2:
                long rowId_c = db.insert(VADatabase.Cls.TABLE_NAME, null, contentValues);
                if (rowId_c > 0) {
                    Uri uriClass = ContentUris.withAppendedId(VADatabase.URI_CLASS, rowId_c);
                    getContext().getContentResolver().notifyChange(uriClass, null);
                    return uriClass;
                }
                break;
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        int count;
        switch (mUriMatcher.match(uri)) {
            case 1:
                count = db.delete(VADatabase.Plugin.TABLE_NAME, selection, selectionArgs);
                break;
            case 2:
                count = db.delete(VADatabase.Cls.TABLE_NAME, selection, selectionArgs);
                break;
            case 3:
                String rowId_p = uri.getPathSegments().get(1);
                count = db.delete(VADatabase.Plugin.TABLE_NAME, VADatabase.Plugin.ID + " = " + rowId_p
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            case 4:
                String rowId_c = uri.getPathSegments().get(1);
                count = db.delete(VADatabase.Cls.TABLE_NAME, VADatabase.Cls.ID + " = " + rowId_c
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        int count;
        switch (mUriMatcher.match(uri)) {
            case 1:
                count = db.update(VADatabase.Plugin.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case 2:
                count = db.update(VADatabase.Cls.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case 3:
                String rowId_p = uri.getPathSegments().get(1);
                count = db.update(VADatabase.Plugin.TABLE_NAME, contentValues, VADatabase.Plugin.ID + " = " + rowId_p +
                        (!TextUtils.isEmpty(selection) ? " AND (" + ")" : ""), selectionArgs);
                break;
            case 4:
                String rowId_c = uri.getPathSegments().get(1);
                count = db.update(VADatabase.Cls.TABLE_NAME, contentValues, VADatabase.Cls.ID + " = " + rowId_c +
                        (!TextUtils.isEmpty(selection) ? " AND (" + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

