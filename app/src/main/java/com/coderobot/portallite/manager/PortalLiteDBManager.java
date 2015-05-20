package com.coderobot.portallite.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tony on 2015/3/9.
 */
public class PortalLiteDBManager extends SQLiteOpenHelper {

    private static PortalLiteDBManager mInstance = null;
    private static int DBVersion = 1;
    private static String DBName = "PortalLite";


    private PortalLiteDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private synchronized PortalLiteDBManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new PortalLiteDBManager(context, DBName, null, DBVersion);

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS User("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "name TEXT,"
                + "password TEXT,"
                + ")";

        String CREATE_SEMESTER_TABLE = "CREATE TABLE IF NOT EXISTS Semesters("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "year INTEGER,"
                + "semester INTEGER,"
                + ")";

        String CREATE_COURSE_TABLE = "CREATE TABLE IF NOT EXISTS Courses("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "courseID TEXT,"
                + "name TEXT,"
                + "type TEXT,"
                + "year INTEGER,"
                + "semester INTEGER,"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SEMESTER_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
