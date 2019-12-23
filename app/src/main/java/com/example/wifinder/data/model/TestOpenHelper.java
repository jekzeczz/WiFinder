package com.example.wifinder.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TestOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "Sotusei.db";
    private static final String TABLE_SPOT = "spot2";
    private static final String TABLE_USER = "user";
    private static final String TABLE_FAVORITE = "favorite";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LONGITUDE = "longitude"; //経度
    private static final String LATITUDE = "latitude"; // 緯度

    private static final String EMAIL = "email";

    private static final String SPOT_ID = "spot";
    private static final String USER_ID = "user";



    /*
    テーブルの作成()
     */
    private static final String SQL_CREATE_SPOTS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SPOT + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT," +
                    LONGITUDE + " REAL," +
                    LATITUDE + " REAL)";

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
                    ID + " INTEGER PRIMARY KEY autoincrement," +
                    NAME + " TEXT," +
                    EMAIL + " TEXT)";
             //       ID + " INTEGER PRIMARY KEY autoincrement)";


    private static final String SQL_CREATE_FAVORITES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE + " (" +
                    ID + " INTEGER PRIMARY KEY autoincrement," +
                    SPOT_ID + " INTEGER," +
                    USER_ID + " INTEGER)";



    /*
    テーブルの削除
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_SPOT;

    public TestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase abc) {
        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される

        abc.execSQL(SQL_CREATE_SPOTS);
        abc.execSQL(SQL_CREATE_USERS);
        abc.execSQL(SQL_CREATE_FAVORITES);

        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }

    /*

     */
    public void saveData(SQLiteDatabase db, int id, String name, double longitude, double latitude){
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, name);
        values.put(LONGITUDE, longitude);
        values.put(LATITUDE, latitude);

        db.insert("spot2", null, values);
    }

    public void saveUser(SQLiteDatabase db, String name, String email){
        ContentValues values = new ContentValues();
        //values.put(ID, 10);
        values.put(NAME, name);
        values.put(EMAIL, email);

        Log.d("#", "userData name " + name + " email " + email);

        db.insert("user", null, values);
    }

    public void saveFavo(SQLiteDatabase db, String name, String spot, int user){
        ContentValues values = new ContentValues();
        values.put(SPOT_ID, spot);
        values.put(USER_ID, user);

        db.insert("favorite", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
