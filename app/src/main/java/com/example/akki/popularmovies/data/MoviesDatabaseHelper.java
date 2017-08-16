package com.example.akki.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.akki.popularmovies.rest.model.movies.AndroidMovies;

/**
 * Created by Akshay on 02-08-2017.
 */

public class MoviesDatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "moviestable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String[] COLUMNS = {MoviesTable.COLUMN_POPULARITY,
            MoviesTable.COLUMN_RATING, MoviesTable.COLUMN_VOTE_COUNT,
            MoviesTable.COLUMN_GENRES, MoviesTable.COLUMN_ID,
            MoviesTable.COLUMN_ORIGINAL_TITLE, MoviesTable.COLUMN_OVERVIEW,
            MoviesTable.COLUMN_RELEASE_DATE, MoviesTable.COLUMN_POSTER_PATH };

    private static MoviesDatabaseHelper instance;

    public static MoviesDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MoviesDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        MoviesTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MoviesTable.onUpgrade(database,oldVersion,newVersion);
    }

    public void addMovie(AndroidMovies movies){

        Log.d("addMovies", movies.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MoviesTable.COLUMN_ID, movies.getId());
        values.put(MoviesTable.COLUMN_ORIGINAL_TITLE, movies.getOriginal_title());
        values.put(MoviesTable.COLUMN_OVERVIEW, movies.getOverview());
        values.put(MoviesTable.COLUMN_RELEASE_DATE, movies.getRelease_date());
        values.put(MoviesTable.COLUMN_GENRES, movies.getGenre_names());
        values.put(MoviesTable.COLUMN_VOTE_COUNT, movies.getVote_count());
        values.put(MoviesTable.COLUMN_RATING, movies.getUser_rating());
        values.put(MoviesTable.COLUMN_POPULARITY, movies.getPopularity());
        values.put(MoviesTable.COLUMN_POSTER_PATH, movies.getPoster_path());

        // 3. insert
        // "INSERT INTO BOOK (TITLE, AUTHOR) VALUES (book.title, book.author)"

        db.insert(MoviesTable.TABLE_MOVIES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public AndroidMovies getMovieDetailsById(int id){


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(MoviesTable.TABLE_MOVIES, // a. table
                        COLUMNS, // b. column names
                        MoviesTable.COLUMN_ID+" = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit


        if (cursor != null)
            cursor.moveToFirst();


        AndroidMovies movie = new AndroidMovies();
        movie.setId(cursor.getInt(0));
        movie.setOriginal_title(cursor.getString(1));
        movie.setOverview(cursor.getString(2));
        movie.setRelease_date(cursor.getString(3));
        movie.setGenre_names(cursor.getString(4));
        movie.setVote_count(cursor.getInt(5));
        movie.setUser_rating(cursor.getFloat(6));
        movie.setPopularity(cursor.getFloat(7));
        movie.setPoster_path(cursor.getString(8));

        Log.d("getMovieById", movie.toString());
        return movie;
    }

}
