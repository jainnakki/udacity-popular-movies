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

package com.example.akki.popularmovies.rest.model.movies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for details of the movie
 */
public class AndroidMovies implements Parcelable {

    private List<Integer> genre_ids = new ArrayList<>();

    private String original_title, poster_path, backdrop_path, overview, release_date, genre_names;

    private Integer id, vote_count, favourite = 0;

    private Float vote_average, popularity;

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

    public AndroidMovies() {
    }

    private AndroidMovies(Parcel in) {

        this.vote_count = in.readInt();
        this.id = in.readInt();

        this.vote_average = in.readFloat();
        this.popularity = in.readFloat();

        this.poster_path = in.readString();
        this.original_title = in.readString();
        this.genre_ids = in.readArrayList(Integer.class.getClassLoader());
        this.genre_names = in.readString();
        this.backdrop_path = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.favourite = in.readInt();
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
        dest.writeString(this.genre_names);
        dest.writeString(this.backdrop_path);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeInt(this.favourite);
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

    public String getGenre_names() {
        return genre_names;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVote_count() {
        return vote_count;
    }

    public Float getUser_rating() {
        return vote_average;
    }

    public Float getPopularity() {
        return popularity;
    }

    public Integer getFavourite() {
        return favourite;
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

    public void setGenre_names(String Genre_names) {
        this.genre_names = Genre_names;
    }

    public void setBackdrop_path(String Backdrop_path) {
        this.backdrop_path = Backdrop_path;
    }

    public void setId(Integer Id) {
        this.id = Id;
    }

    public void setVote_count(Integer Vote_Count) {
        this.vote_count = Vote_Count;
    }

    public void setUser_rating(Float Vote_average) {
        this.vote_average = Vote_average;
    }

    public void setPopularity(Float Popularity) {
        this.popularity = Popularity;
    }

    public void setFavourite(int newFavourite) {
        this.favourite = newFavourite;
    }

}
