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

package com.example.akki.popularmovies.rest.model.video;

import com.example.akki.popularmovies.rest.model.video.MovieVideo;

import java.util.ArrayList;

/**
 * Created by Akshay on 29-07-2017.
 */

public class MovieVideoList {

    Integer id;

    private ArrayList<MovieVideo> results;

    public ArrayList<MovieVideo> getResults() {
        return results;
    }
}
