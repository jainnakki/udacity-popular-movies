package com.example.akki.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Akshay on 20-07-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<String> urls = new ArrayList<String>();;
    private Context context;
    List<AndroidMovies>MoviesList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_image)
        public ImageView moviePoster;
        @BindView(R.id.progressBar)
        public ProgressBar progressBar = null;

        public View layout;


        public ViewHolder(View v){
            super(v);
            layout = v;
            ButterKnife.bind(this,v);
        }

    }

    private AndroidMovies addDummyData() {
        AndroidMovies MovieDummyData = new AndroidMovies();
        MovieDummyData.setOriginal_title("----");
        //MovieDummyData.setPoster_path("drawable/not_connected.png");
        MovieDummyData.setOverview("----");
        MovieDummyData.setUser_rating(0.0f);
        MovieDummyData.setRelease_date("----");

        return MovieDummyData;
    }

    public MovieAdapter(Context context, String[] MyURLS) {
        super();
        this.context = context;
        urls = Arrays.asList(MyURLS);
        this.MoviesList = new ArrayList<>();
        this.MoviesList.add(addDummyData());
    }

    public void setMoviesList(List<AndroidMovies> movieList) {
        this.MoviesList = movieList;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.movie_item,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final AndroidMovies movies = MoviesList.get(position);

        if (AppStatus.getInstance(context).isOnline()) {
        holder.progressBar.setVisibility(View.VISIBLE);}
        Log.i("POSTER PATH: ","http://image.tmdb.org/t/p/w185" + movies.getPoster_path());
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185" + movies.getPoster_path())
                //.resize(400, 400)
                .fit()
                .error(R.drawable.error)
                .into(holder.moviePoster, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess(){
                        if (holder.progressBar != null) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });

        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetails.class);

                AndroidMovies movie = new AndroidMovies();
                movie.setOriginal_title(movies.getOriginal_title());
                movie.setPoster_path(movies.getPoster_path());
                movie.setOverview(movies.getOverview());
                movie.setUser_rating(movies.getUser_rating());
                movie.setRelease_date(movies.getRelease_date());
                intent.putExtra("movie_details", movie);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (MoviesList == null)? 0 : MoviesList.size();
    }

}
