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

package com.example.akki.popularmovies.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.akki.popularmovies.R;
import com.example.akki.popularmovies.rest.model.review.MovieReview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Custom adapter for movie reviews
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private List<MovieReview> MovieReviewList;


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        public TextView movieReviewAuthor;
        @BindView(R.id.review_content)
        public TextView movieReviewContent;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }

    }

    private MovieReview addDummyMovieReviewData() {
        MovieReview ReviewDummyData = new MovieReview();
        ReviewDummyData.setAuthor("----");
        ReviewDummyData.setContent("----");

        return ReviewDummyData;
    }

    public MovieReviewAdapter(Context context) {
        super();
        //Context context1 = context;
        this.MovieReviewList = new ArrayList<>();
        this.MovieReviewList.add(addDummyMovieReviewData());
    }

    public void setMovieReviewList(List<MovieReview> reviewList) {
        this.MovieReviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.movie_review, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final MovieReview movieReview = MovieReviewList.get(position);

        holder.movieReviewAuthor.setText(movieReview.getAuthor());
        holder.movieReviewContent.setText(movieReview.getContent());
    }


    @Override
    public int getItemCount() {
        return (MovieReviewList == null) ? 0 : MovieReviewList.size();
    }

}
