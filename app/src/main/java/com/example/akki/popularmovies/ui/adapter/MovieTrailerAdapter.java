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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.akki.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Custom Adapter for movie trailer view
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {

    private final Context context;

    private String[] videoKeys;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_thumbnail_view)
        public ImageView trailerImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    public MovieTrailerAdapter(Context context) {
        super();
        this.context = context;
    }


    public void setVideoKeys(String videoKeys[]) {
        this.videoKeys = videoKeys;
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.movie_trailer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String trailerThumbnailImage = "https://img.youtube.com/vi/" + videoKeys[position] + "/0.jpg";

        Glide.with(context)
                .load(trailerThumbnailImage)
                .error(R.drawable.placeholder_trailer_thumbnail)
                .centerCrop()
                .into(holder.trailerImageView);

        holder.trailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = holder.getAdapterPosition();
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKeys[pos]));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + videoKeys[pos]));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return (videoKeys == null) ? 0 : videoKeys.length;
    }

}
