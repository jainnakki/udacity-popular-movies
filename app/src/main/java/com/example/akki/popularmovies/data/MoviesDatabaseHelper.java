package com.example.akki.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Akshay on 02-08-2017.
 */

public class MoviesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviestable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String[] COLUMNS = {MoviesTable.COLUMN_POPULARITY,
            MoviesTable.COLUMN_RATING, MoviesTable.COLUMN_VOTE_COUNT,
            MoviesTable.COLUMN_GENRES, MoviesTable.COLUMN_ID,
            MoviesTable.COLUMN_ORIGINAL_TITLE, MoviesTable.COLUMN_OVERVIEW,
            MoviesTable.COLUMN_RELEASE_DATE, MoviesTable.COLUMN_POSTER_PATH};

    private static MoviesDatabaseHelper instance;

    public static MoviesDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MoviesDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        MoviesTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MoviesTable.onUpgrade(database, oldVersion, newVersion);
    }

}
