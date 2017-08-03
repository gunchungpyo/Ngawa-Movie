package com.viv.gunchung.ngawamovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viv.gunchung.ngawamovie.R;
import com.viv.gunchung.ngawamovie.models.Movie;
import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

import java.util.List;

/**
 * Created by Gunawan on 07/07/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMovieList;

    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterImageView;
        public final TextView mMoviesTitleTextView;
        public final TextView mMoviesRatingTextView;

       public MoviesAdapterViewHolder(View view) {
           super(view);
           mMoviesTitleTextView = (TextView) view.findViewById(R.id.tv_movies_title);
           mMoviesRatingTextView = (TextView) view.findViewById(R.id.tv_movies_rating);
           mPosterImageView = (ImageView) view.findViewById(R.id.iv_movies_poster);

           mPosterImageView.setOnClickListener(this);
       }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MoviesAdapterViewHolder viewHolder = new MoviesAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);

        holder.mMoviesTitleTextView.setText(movie.getTitle());
        holder.mMoviesRatingTextView.setText(String.valueOf(movie.getVoteAverage()));

        String moviePosterUrl = MovieUtils.generateImgUrl(movie.getPosterPath());
        Picasso.with(mContext).load(moviePosterUrl).into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }
}
