package com.example.wifinder.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wifinder.data.model.Spots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpotsAdapter {
    protected static final String TAG = "DataAdapter";

    // TODO : TABLE名を記載すること
    protected static final String TABLE_NAME = "spots";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public SpotsAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public SpotsAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public SpotsAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public List getTableData()
    {
        try
        {
            // Table 이름 -> antpool_bitcoin 불러오기
            String sql ="SELECT * FROM " + TABLE_NAME;

            // モデルを入れるリスト生成
            List spotsList = new ArrayList();

            // TODO : モデル宣言
            Spots spots = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // カラムの最後まで
                while( mCur.moveToNext() ) {

                    // TODO : カスタムモデルの生成
                    spots = new Spots();

                    // TODO : Record 記述
                    // id, name, account, privateKey, secretKey, Comment
                    spots.setId(mCur.getInt(0));
                    spots.setName(mCur.getString(1));
                    spots.setAddress(mCur.getString(2));
                    spots.setLatitude(mCur.getDouble(3));
                    spots.setLongitude(mCur.getDouble(4));

                    // リストに入れる
                    spotsList.add(spots);
                }

            }
            return spotsList;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
}
