package com.example.wifinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created 2018/09/24.
 * 参考：https://akira-watson.com/android/sqlite.html
 */
public class TestOpenDB extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 3;

    // データーベース情報を変数に格納
    private static final String DATABASE_NAME = "Test.db";
    private static final String TABLE_NAME = "spot2";
    private static final String ID = "id";
    private static final String COLUMN_NAME_TITLE = "name";
    private static final String COLUMN_NAME_A = "longitude";
    private static final String COLUMN_NAME_B = "latitude";
    private static final String COLUMN_NAME_C = "type";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + "INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + "TEXT," +
                    COLUMN_NAME_A + " REAL," +
                    COLUMN_NAME_B + " REAL," +
                    COLUMN_NAME_C + " INTGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    TestOpenDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

        saveData(db, "music1", 10);
        saveData(db, "music2", 0);
        saveData(db, "music3", 0);
        saveData(db, "music4", 0);
        saveData(db, "music5", 0);

    }

    // 参考：https://sankame.github.io/blog/2017-09-05-android_sqlite_db_upgrade/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void saveData(SQLiteDatabase db, String title, int score){
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("score", score);

        db.insert("testdb", null, values);
    }
}
