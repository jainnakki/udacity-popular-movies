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
import com.example.akki.popularmovies.R;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Akshay on 20-07-2017.
 */

public class MovieTrailerAdapter1 extends RecyclerView.Adapter<MovieTrailerAdapter1.ViewHolder> {

    private final Context context;

    String videoKeys[];

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_thumbnail_view)
        public ImageView trailerImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    public MovieTrailerAdapter1(Context context) {
        super();
        this.context = context;
    }


    public void setVideoKeys(String videoKeys[]) {
        this.videoKeys = videoKeys;
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailerAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.movie_trailer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String trailerThumbnailImage = "https://img.youtube.com/vi/" + videoKeys[position] + "/0.jpg";
        Picasso.with(context)
                .load(trailerThumbnailImage)
                .fit()
                .error(R.drawable.trailer_error)
                .into(holder.trailerImageView);

        //listening to image click
        holder.trailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKeys[position]));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + videoKeys[position]));
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
