package com.example.ahmed.movieapp2.Data.Movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahmed on 2/15/2018.
 */

public class TableData extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "db";
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POSTER = "movie_poster";
    public static final String COLUMN_TITLE = "movie_title";
    public static final String COLUMN_OVERVIEW = "movie_overview";
    public static final String COLUMN_DATE = "release_date";
    public static final String COLUMN_RATING = "movie_rating";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final int VERSION = 1;


    public TableData(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_POSTER + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                COLUMN_RATING + " TEXT" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
