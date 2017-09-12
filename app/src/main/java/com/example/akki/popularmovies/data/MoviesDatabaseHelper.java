/*
 * Copyright 2017.  Akshay Jain
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.akki.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite OpenHelper for Movies
 */

public class MoviesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviestable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String[] COLUMNS = {MoviesTable.COLUMN_POPULARITY,
            MoviesTable.COLUMN_RATING, MoviesTable.COLUMN_VOTE_COUNT,
            MoviesTable.COLUMN_GENRES, MoviesTable.COLUMN_ID,
            MoviesTable.COLUMN_ORIGINAL_TITLE, MoviesTable.COLUMN_OVERVIEW,
            MoviesTable.COLUMN_RELEASE_DATE, MoviesTable.COLUMN_POSTER_PATH, MoviesTable.COLUMN_BACKDROP_PATH};

    private static MoviesDatabaseHelper sInstance;

    public static MoviesDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MoviesDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
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
