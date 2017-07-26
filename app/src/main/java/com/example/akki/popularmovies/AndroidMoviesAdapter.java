package com.example.akki.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Akki on 05-09-2016.
 */
public class AndroidMoviesAdapter extends BaseAdapter {
    private static final String LOG_TAG = AndroidMoviesAdapter.class.getSimpleName();
    private final Context context;
    private final List<String> urls = new ArrayList<String>();


    public AndroidMoviesAdapter(Context context, String[] movieUrl) {
        this.context = context;
        Collections.addAll(urls, movieUrl);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //convertView = new ImageView(context);
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.movie_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_image);

        String url = getItem(position);

        if (url.contains("not_connected.png")) {
            imageView.setImageResource(R.drawable.not_connected);
        } else
            Picasso.with(context).load(url).resize(400, 400).into(imageView);

        return convertView;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replace(List<String> urls) {
        this.urls.clear();
        this.urls.addAll(urls);
        notifyDataSetChanged();
    }
}
