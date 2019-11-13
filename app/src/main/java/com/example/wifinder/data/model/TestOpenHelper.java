package com.example.wifinder.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TestOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "Sotusei.db";
    private static final String TABLE_NAME = "spot2";
    private static final String ID = "id";
    private static final String COLUMN_NAME_TITLE = "name";
    private static final String COLUMN_NAME_A = "longitude"; //経度
    private static final String COLUMN_NAME_B = "latitude"; // 緯度

    /*
    テーブルの作成
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + "INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + "TEXT," +
                    COLUMN_NAME_A + " REAL," +
                    COLUMN_NAME_B + " REAL)";

    /*
    テーブルの削除
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase abc) {
        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        abc.execSQL(
                SQL_CREATE_ENTRIES
        );

        saveData(abc, 4, "東京都大田区羽田空港3-3-2 羽田空港国内線第2旅客ターミナル", 35.551001, 139.788613);
        saveData(abc, 5, "東京都大田区羽田空港2-6-5", 35.54882, 139.783971);
        saveData(abc, 6, "東京都豊島区東池袋3-1-1", 35.54577, 139.768664);
        saveData(abc, 7, "東京都千代田区丸の内1-6-4", 35.729493, 139.718283);
        saveData(abc, 8, "東京都千代田区丸の内1-5-1", 35.684017, 139.766645);
        saveData(abc, 9, "東京都千代田区丸の内2-4-1", 35.682543, 139.764287);

        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }


    /*

     */
    public void saveData(SQLiteDatabase db, int id, String name, double longitude, double latitude){
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("latitude", longitude);
        values.put("longitude", latitude);

        db.insert("spot2", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
