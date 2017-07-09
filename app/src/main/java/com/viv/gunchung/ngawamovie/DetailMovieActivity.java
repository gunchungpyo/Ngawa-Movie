package com.viv.gunchung.ngawamovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

public class DetailMovieActivity extends AppCompatActivity {

    private Movie selectedMovie;

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseYearTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        mPosterImageView = (ImageView) findViewById(R.id.iv_detail_movies_poster);
        mTitleTextView = (TextView) findViewById(R.id.tv_detail_movies_title);
        mRatingTextView = (TextView) findViewById(R.id.tv_detail_movies_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mReleaseYearTextView = (TextView) findViewById(R.id.tv_release_year);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MOVIE_TITLE")) {
                String title = intentThatStartedThisActivity.getStringExtra("MOVIE_TITLE");
                mTitleTextView.setText(title);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_VOTE_AVERAGE")) {
                double voteAverage = intentThatStartedThisActivity.getDoubleExtra("MOVIE_VOTE_AVERAGE", 0);
                mRatingTextView.setText(String.valueOf(voteAverage));
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_POSTER_PATH")) {
                String posterPath = intentThatStartedThisActivity.getStringExtra("MOVIE_POSTER_PATH");
                String moviePosterUrl = MovieUtils.generateImgUrl(posterPath);

                mPosterImageView.bringToFront();
                Picasso.with(this).load(moviePosterUrl).into(mPosterImageView);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_OVERVIEW")) {
                String overview = intentThatStartedThisActivity.getStringExtra("MOVIE_OVERVIEW");
                mOverviewTextView.setText(overview);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_RELEASE_YEAR")) {
                int releaseYear = intentThatStartedThisActivity.getIntExtra("MOVIE_RELEASE_YEAR", 0);
                mReleaseYearTextView.setText(String.valueOf(releaseYear));
            }
        }
    }
}
