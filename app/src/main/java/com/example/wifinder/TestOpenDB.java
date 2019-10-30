package com.example.wifinder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wifinder.data.URLS;
import com.example.wifinder.data.model.Spot;
import com.example.wifinder.data.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    //private static final String COLUMN_NAME_C = "type";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + "INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + "TEXT," +
                    COLUMN_NAME_A + " REAL," +
                    COLUMN_NAME_B + " REAL)";
       //             COLUMN_NAME_B + " REAL," +
       //             COLUMN_NAME_C + " INTGER)";

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
        //saveData(db,"1", "東京都大田区羽田空港3-3-2 羽田", "35.551001", "139.788613");

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

    public void saveData(SQLiteDatabase db, int id, String name, double lo, double la){
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("longitude", lo);
        values.put("latitude", la);


        db.insert("testdb", null, values);
    }






    private class GetSpot extends AsyncTask<Void, Void, String> {

        @Override
        // doInBackgroundメソッドの実行前にメインスレッドで実行されます
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar = findViewById(R.id.loading);
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        // doInBackgroundメソッドの実行後にメインスレッドで実行されます
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //レスポンスをjsonオブジェクトに変換
                JSONObject obj = new JSONObject(s);
                Log.d("#", "JSON Object : " + obj);
                //レスポンスにエラーが無い場合
                if (!obj.getBoolean("error")) {

                    //レスポンスからuserをゲット
                    JSONObject userJson = obj.getJSONObject("spot");

                    //userオブジェクトを生成
                    Spot spot = new Spot(
                            userJson.getInt("id"),
                            userJson.getString("spotname"),
                            userJson.getDouble("latitude"),
                            userJson.getDouble("longitude")
                    );

                    Log.d("##", "id : " + spot.getId());
                    Log.d("##", "username : " + spot.getSpotname());

                    //storing the user in shared preferences
                    //PrefManager.getInstance(getApplicationContext()).setUserLogin(user);
                    Log.d("##", "###");
                    //マップ画面に遷移
                    //finish();
                    //Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    //startActivity(intent);

                } else {
                    //Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            HashMap<String, String> params = new HashMap<>();

            return requestHandler.sendPostRequest(URLS.SPOT_ROOT, params);
        }
    }
}
