package com.example.akki.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akki on 05-09-2016.
 */
public class AndroidMovies implements Parcelable {

    private List<Integer>genre_ids;

    private String original_title,poster_path,overview,release_date;

    private int id,vote_count;

    private float vote_average,popularity;

    public static final Creator<AndroidMovies> CREATOR = new Creator<AndroidMovies>() {
        @Override
        public AndroidMovies createFromParcel(Parcel in) {
            return new AndroidMovies(in);
        }

        @Override
        public AndroidMovies[] newArray(int size) {
            return new AndroidMovies[size];
        }
    };

    public AndroidMovies(){}

    public AndroidMovies(Parcel in) {

        this.vote_count = in.readInt();
        this.id = in.readInt();

        this.vote_average = in.readFloat();
        this.popularity = in.readFloat();

        this.poster_path = in.readString();
        this.original_title = in.readString();
        this.genre_ids = in.readArrayList(Integer.class.getClassLoader());
        this.overview = in.readString();
        this.release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.vote_count);
        dest.writeInt(this.id);

        dest.writeFloat(this.vote_average);
        dest.writeFloat(this.popularity);

        dest.writeString(this.poster_path);
        dest.writeString(this.original_title);
        dest.writeList(this.genre_ids);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
    }



    //getter methods
    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public int getVote_count() { return vote_count; }

    public float getUser_rating() {
        return vote_average;
    }

    public float getPopularity() {
        return popularity;
    }

    //setter methods

    public void setOriginal_title(String OriginalTitle) {
        this.original_title = OriginalTitle;
    }

    public void setPoster_path(String PosterPath) {
        this.poster_path = PosterPath;
    }

    public void setOverview(String Overview) {
        this.overview = Overview;
    }

    public void setRelease_date(String ReleaseDate) {
        this.release_date = ReleaseDate;
    }

    public void setGenre_ids(List<Integer> Genre_ids) {
        this.genre_ids = Genre_ids;
    }

    public void setId(int Id) {
        this.id = Id;
    }

    public void setVote_count(int Vote_Count) { this.vote_count = Vote_Count; }

    public void setUser_rating(float Vote_average) {
        this.vote_average = Vote_average;
    }

    public void setPopularity(float Popularity) {
        this.popularity = Popularity;
    }
}
