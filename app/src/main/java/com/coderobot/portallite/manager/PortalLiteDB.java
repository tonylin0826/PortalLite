package com.coderobot.portallite.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.coderobot.portallite.model.data.ClassTime;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.CourseInfo;
import com.coderobot.portallite.model.data.Homework;
import com.coderobot.portallite.model.data.Material;
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
    public static final String TABLE_COURSE_INFO = "CourseInfo";
    public static final String TABLE_MATERIAL = "Material";
    public static final String TABLE_HOMEWORK = "Homework";
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

    private static final String CREATE_COURSE_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE_INFO + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id TEXT NOT NULL, " +
            "attachment TEXT, " +
            "author TEXT, " +
            "date TEXT NOT NULL, " +
            "detail TEXT NOT NULL, " +
            "subject TEXT NOT NULL)";

    private static final String CREATE_MATERIAL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MATERIAL + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id TEXT NOT NULL, " +
            "attachment TEXT, " +
            "course_schedule TEXT NOT NULL, " +
            "date TEXT NOT NULL, " +
            "detail TEXT NOT NULL)";

    private static final String CREATE_HOMEWORK_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HOMEWORK + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id TEXT NOT NULL, " +
            "attachment TEXT, " +
            "course_schedule TEXT NOT NULL, " +
            "deadline TEXT NOT NULL, " +
            "detail TEXT NOT NULL, " +
            "grade TEXT NOT NULL, " +
            "subject TEXT NOT NULL, " +
            "uploaded_file TEXT)";

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
        } else if (obj instanceof CourseInfo) {
            insertCourseInfo((CourseInfo) obj);
        } else if (obj instanceof Material) {
            insertMaterial((Material) obj);
        } else if (obj instanceof Homework) {
            insertHomework((Homework) obj);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEMESTER_TABLE);
        db.execSQL(CREATE_COURSE_TABLE);
        db.execSQL(CREATE_COURSE_INFO_TABLE);
        db.execSQL(CREATE_MATERIAL_TABLE);
        db.execSQL(CREATE_HOMEWORK_TABLE);
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


    private void insertCourseInfo(CourseInfo courseInfo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", courseInfo.id);
        values.put("attachment", courseInfo.attachment);
        values.put("author", courseInfo.author);
        values.put("date", courseInfo.date);
        values.put("detail", courseInfo.detail);
        values.put("subject", courseInfo.subject);

        db.insert(TABLE_COURSE_INFO, null, values);
    }

    private void insertMaterial(Material material) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", material.id);
        values.put("course_schedule", material.course_schedule);
        values.put("attachment", material.attachment);
        values.put("date", material.date);
        values.put("detail", material.detail);

        db.insert(TABLE_MATERIAL, null, values);
    }

    private void insertHomework(Homework homework) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", homework.id);
        values.put("course_schedule", homework.course_schedule);
        values.put("attachment", homework.attachment);
        values.put("deadline", homework.deadline);
        values.put("detail", homework.detail);

        // todo ask andy
        values.put("grade", homework.grade);
        values.put("subject", homework.subject);
        values.put("uploaded_file", homework.uploaded_file);

        db.insert(TABLE_HOMEWORK, null, values);
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

        cursor = db.rawQuery("select * from CourseInfo", null);

        while (cursor.moveToNext()) {
            log("id = " + cursor.getString(1));
            log("attachment = " + cursor.getString(2));
            log("author = " + cursor.getString(3));
            log("date = " + cursor.getString(4));
            log("detail = " + cursor.getString(5));
            log("subject = " + cursor.getString(6));
        }

        cursor = db.rawQuery("select * from Material", null);

        while (cursor.moveToNext()) {
            log("id = " + cursor.getString(1));
            log("attachment = " + cursor.getString(2));
            log("course_schedule = " + cursor.getString(3));
            log("date = " + cursor.getString(4));
            log("detail = " + cursor.getString(5));
        }

        cursor = db.rawQuery("select * from Homework", null);

        while (cursor.moveToNext()) {
            log("id = " + cursor.getString(1));
            log("attachment = " + cursor.getString(2));
            log("course_schedule = " + cursor.getString(3));
            log("deadline = " + cursor.getString(4));
            log("detail = " + cursor.getString(5));
            log("grade = " + cursor.getString(6));
            log("subject = " + cursor.getString(7));
            log("uploaded_file = " + cursor.getString(8));
        }

        cursor.close();
    }


    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
