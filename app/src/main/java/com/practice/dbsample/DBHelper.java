package com.practice.dbsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by spartans on 20/4/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MemoryGameScore.db";
    public static final String SCORE_TABLE_NAME = "ScoreTable";
    public static final String NAME_COLUMN_ID = "name";
    public static final String SCORE_COLUMN_NAME = "score";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(getClass().getSimpleName(), "onCreate() called.");
        db.execSQL("CREATE TABLE " + SCORE_TABLE_NAME +
                " (" + NAME_COLUMN_ID + " TEXT" +
                "," + SCORE_COLUMN_NAME + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertRecord(String name, int Score) {
        Log.d(getClass().getSimpleName(), "insertRecord() called.");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_ID, name);
        contentValues.put(SCORE_COLUMN_NAME, ""+Score);
        db.insert(SCORE_TABLE_NAME, null, contentValues);
    }

    public ArrayList<String> getRecords() {
        Log.d(getClass().getSimpleName(), "getRecords() called.");
        ArrayList<String> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+SCORE_TABLE_NAME+" order by "+SCORE_COLUMN_NAME+" desc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            scoreList.add(res.getString(res.getColumnIndex(NAME_COLUMN_ID))
                + "," +res.getString(res.getColumnIndex(SCORE_COLUMN_NAME)));
            res.moveToNext();
        }
        return scoreList;
    }

    public ArrayList<Integer> getDistinctScores() {
        Log.d(getClass().getSimpleName(), "getRecords() called.");
        ArrayList<Integer> distinctScore = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select distinct "+SCORE_COLUMN_NAME+" from "+SCORE_TABLE_NAME+" order by "+SCORE_COLUMN_NAME+" desc", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            distinctScore.add(res.getInt(res.getColumnIndex(SCORE_COLUMN_NAME)));
            res.moveToNext();
        }

        Log.i(getClass().getSimpleName(), "distinctScore :: "+distinctScore.toString());
        return distinctScore;
    }
}
