package com.example.akki.popularmovies.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.akki.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Akshay on 22-08-2017.
 */

public class MovieTrailerAdapter extends PagerAdapter {
    Context context;
    String videoKeys[];
    LayoutInflater layoutInflater;

    public MovieTrailerAdapter(Context context, String videoKeys[]) {
        this.context = context;
        this.videoKeys = videoKeys;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return videoKeys.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.movie_trailer, container, false);

        ImageView trailerImageView = (ImageView)itemView.findViewById(R.id.trailer_thumbnail_view);

        String trailerThumbnailImage = "https://img.youtube.com/vi/" + videoKeys[position] + "/0.jpg";
        Picasso.with(context)
                .load(trailerThumbnailImage)
                .fit()
                .error(R.drawable.trailer_error)
                .into(trailerImageView);

        container.addView(itemView);

        //listening to image click
        trailerImageView.setOnClickListener(new View.OnClickListener() {
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

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
