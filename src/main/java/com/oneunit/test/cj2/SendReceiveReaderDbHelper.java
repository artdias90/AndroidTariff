package com.oneunit.test.cj2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by artdias90 on 23/05/2016.
 */
public class SendReceiveReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME ="SendReceiveReader.db";

    public SendReceiveReaderDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB: ", "--- onCreate database ---");
        db.execSQL(SendReceiveReaderContract.FeedEntry.SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SendReceiveReaderContract.FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);

    }
}
