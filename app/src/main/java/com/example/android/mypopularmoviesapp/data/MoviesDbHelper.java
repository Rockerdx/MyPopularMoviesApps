package com.example.android.mypopularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 4;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + MoviesContract.MoviesDB.TABLE_NAME + " (" +
                MoviesContract.MoviesDB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MoviesDB.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesDB.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MoviesContract.MoviesDB.COLUMN_MOVIE_LANGUANGE + " TEXT NOT NULL," +
                MoviesContract.MoviesDB.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                MoviesContract.MoviesDB.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                MoviesContract.MoviesDB.COLUMN_MOVIE_REVIEW + " TEXT NOT NULL," +
                MoviesContract.MoviesDB.COLUMN_MOVIE_IMAGE + " BLOB" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesDB.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
