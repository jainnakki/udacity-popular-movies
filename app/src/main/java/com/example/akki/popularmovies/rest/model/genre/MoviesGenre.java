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

package com.example.akki.popularmovies.rest.model.genre;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for movies genre
 */

public class MoviesGenre implements Parcelable {

    private Integer id;
    private String name;

    public MoviesGenre() {
    }

    private MoviesGenre(Parcel in) {
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
