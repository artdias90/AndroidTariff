package com.oneunit.test.cj2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Arthur Dias on 16;05.2016
 */

public class NetworkAnalysisDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME ="NetworkAnalysis.db";

    public NetworkAnalysisDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("mylogs", "--- onCreate database ---");
        db.execSQL(NetworkAnalysisDbContract.FeedEntry.SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NetworkAnalysisDbContract.FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);

    }
}