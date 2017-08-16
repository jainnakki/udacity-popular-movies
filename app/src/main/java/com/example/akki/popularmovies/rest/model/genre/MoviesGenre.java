package com.example.akki.popularmovies.rest.model.genre;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshay on 27-07-2017.
 */

public class MoviesGenre implements Parcelable {

    private Integer id;
    private String name;

    public MoviesGenre(){}

    public MoviesGenre(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<MoviesGenre> CREATOR = new Creator<MoviesGenre>() {
        @Override
        public MoviesGenre createFromParcel(Parcel in) {
            return new MoviesGenre(in);
        }

        @Override
        public MoviesGenre[] newArray(int size) {
            return new MoviesGenre[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
