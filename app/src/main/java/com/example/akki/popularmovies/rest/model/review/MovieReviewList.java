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

package com.example.akki.popularmovies.rest.model.review;

import com.example.akki.popularmovies.rest.model.review.MovieReview;

import java.util.ArrayList;

/**
 * Created by Akshay on 28-07-2017.
 */

public class MovieReviewList {

    private Integer id;
    private Integer page;
    private ArrayList<MovieReview> results;
    private Integer total_pages;
    private Integer total_results;

    public ArrayList<MovieReview> getResults() {
        return results;
    }

}
