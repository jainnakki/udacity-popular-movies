package com.example.akki.popularmovies.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Akshay on 02-08-2017.
 */

public class MoviesTable {

    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ORIGINAL_TITLE = "title";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_RELEASE_DATE = "releaseDate";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_VOTE_COUNT = "voteCount";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_POSTER_PATH = "posterPath";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_MOVIES
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_ORIGINAL_TITLE + " text not null, "
            + COLUMN_OVERVIEW + " text not null,"
            + COLUMN_RELEASE_DATE + " date not null,"
            + COLUMN_GENRES + " text not null,"
            + COLUMN_VOTE_COUNT + " integer not null,"
            + COLUMN_RATING + " real not null,"
            + COLUMN_POPULARITY + " integer not null,"
            + COLUMN_POSTER_PATH + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(MoviesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(database);
    }
}