package com.example.akki.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Akshay on 20-07-2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private Context context;

    List<MovieReview>MovieReviewList;


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        public TextView movieReviewAuthor;
        @BindView(R.id.review_content)
        public TextView movieReviewContent;

        public View layout;


        public ViewHolder(View v){
            super(v);
            layout = v;
            ButterKnife.bind(this,v);
        }

    }

    private MovieReview addDummyMovieReviewData() {
        MovieReview ReviewDummyData = new MovieReview();
        ReviewDummyData.setAuthor("----");
        //MovieDummyData.setPoster_path("drawable/not_connected.png");
        ReviewDummyData.setContent("----");

        return ReviewDummyData;
    }

    public MovieReviewAdapter(Context context) {
        super();
        this.context = context;
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
        View v = inflater.inflate(R.layout.movie_review,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final MovieReview movieReview = MovieReviewList.get(position);

        //if (AppStatus.getInstance(context).isOnline()) {
          //  holder.progressBar.setVisibility(View.VISIBLE);}

        holder.movieReviewAuthor.setText(movieReview.getAuthor());
        holder.movieReviewContent.setText(movieReview.getContent());
    }


    @Override
    public int getItemCount() {
        return (MovieReviewList == null)? 0 : MovieReviewList.size();
    }

}
