package com.coderobot.portallite.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.coderobot.portallite.model.data.ClassTime;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by the great Tony on 2015/5/22.
 */
public class PortalLiteDB extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "PortalLiteDB.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_SEMESTER = "Semester";
    public static final String TABLE_COURSE = "Course";
    private static final String TAG = "PortalLiteDB";

    private static PortalLiteDB instance = null;
    private static final String CREATE_SEMESTER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SEMESTER + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "year INTEGER NOT NULL, " +
            "semester INTEGER NOT NULL)";

    /**
     * Do not mix _id with id, id means course id
     */
    private static final String CREATE_COURSE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id TEXT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "ctype TEXT NOT NULL, " +
            "ctimes TEXT NOT NULL, " +
            "classroom TEXT NOT NULL, " +
            "semester TEXT NOT NULL)";

    public static PortalLiteDB getInstance(Context context) {
        if (instance == null)
            instance = new PortalLiteDB(context, DATABASE_NAME, null, DATABASE_VERSION);
        return instance;
    }

    public PortalLiteDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void insert(Object obj) {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            int size = list.size();
            for (int i = 0; i < size; i++) {
                insert(list.get(i));
            }

        } else if (obj instanceof Semester) {
            insertSemester((Semester) obj);
        } else if (obj instanceof Course) {
            insertCourse((Course) obj);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEMESTER_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertSemester(Semester semester) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("year", semester.year);
        values.put("semester", semester.semester);

        db.insert(TABLE_SEMESTER, null, values);
    }

    private void insertCourse(Course course) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", course.id);
        values.put("name", course.name);
        values.put("ctype", course.ctype);
        values.put("ctimes", CommonUtil.classTimeListToString(course.ctimes));
        values.put("classroom", course.classroom);
        values.put("semester", course.semester.toString());

        db.insert(TABLE_COURSE, null, values);
    }

    public ArrayList<Course> getCourses(Semester semester) {
        ArrayList<Course> courses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Course where semester=?", new String[] {semester.year + "/" + semester.semester});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.id = cursor.getString(1);
            course.name = cursor.getString(2);
            course.ctype = cursor.getString(3);
            List<String> tmp = Arrays.asList(cursor.getString(4).split("__,__"));
            course.ctimes = new ArrayList<>();
            for (String str : tmp) {
                if (!str.isEmpty())
                    course.ctimes.add(new ClassTime(str));
            }
            course.classroom = cursor.getString(5);
            course.semester = new Semester(cursor.getString(6));

            courses.add(course);
        }
        cursor.close();
        return courses;

    }

    public void logAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Semester", null);

        while (cursor.moveToNext()) {
            log("year = " + cursor.getString(1));
            log("semester = " + cursor.getString(2));
        }

        cursor.close();

        cursor = db.rawQuery("select * from Course", null);

        while (cursor.moveToNext()) {
            log("id = " + cursor.getString(1));
            log("name = " + cursor.getString(2));
            log("ctype = " + cursor.getString(3));
            log("ctimes = " + cursor.getString(4));
            log("classroom = " + cursor.getString(5));
            log("semester = " + cursor.getString(6));
        }

        cursor.close();
    }


    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
