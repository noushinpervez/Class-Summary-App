package edu.ewubd.cse489_sec2_2020160189;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClassSummaryDB extends SQLiteOpenHelper {

    public ClassSummaryDB(Context context) {
        super(context, "ClassSummaryDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE lectures  (" + "ID TEXT PRIMARY KEY," + "course TEXT," + "type TEXT," + "date INT," + "lecture INT," + "topic TEXT," + "summary TEXT" + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertLecture(String ID, String course, String type, long date, int lecture, String topic, String summary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("ID", ID);
        cols.put("course", course);
        cols.put("type", type);
        cols.put("date", date);
        cols.put("lecture", lecture);
        cols.put("topic", topic);
        cols.put("summary", summary);

        db.insert("lectures", null, cols);
        db.close();
    }

    public void updateLecture(String ID, String course, String type, long date, int lecture, String topic, String summary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("ID", ID);
        cols.put("course", course);
        cols.put("type", type);
        cols.put("date", date);
        cols.put("lecture", lecture);
        cols.put("topic", topic);
        cols.put("summary", summary);

        db.update("lectures", cols, "ID = ?", new String[]{ID});
        db.close();
    }

    public void deleteLecture(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("lectures", "ID = ?", new String[]{ID});
        db.close();
    }

    public Cursor selectLectures(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = null;

        try {
            cur = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cur;
    }

    public ClassSummary getLectureById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ClassSummary lecture = null;

        Cursor cursor = db.query("lectures", null, "ID = ?", new String[]{id}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String courseId = cursor.getString(cursor.getColumnIndex("course"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            long date = cursor.getLong(cursor.getColumnIndex("date"));
            int lectureNumber = cursor.getInt(cursor.getColumnIndex("lecture"));
            String topic = cursor.getString(cursor.getColumnIndex("topic"));
            String summary = cursor.getString(cursor.getColumnIndex("summary"));

            lecture = new ClassSummary(id, courseId, type, date, lectureNumber, topic, summary);
        }

        if (cursor != null) cursor.close();

        return lecture;
    }
}