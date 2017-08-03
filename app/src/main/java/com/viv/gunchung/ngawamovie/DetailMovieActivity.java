package com.viv.gunchung.ngawamovie;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viv.gunchung.ngawamovie.adapter.MoviesReviewAdapter;
import com.viv.gunchung.ngawamovie.adapter.MoviesVideoAdapter;
import com.viv.gunchung.ngawamovie.data.MovieContract;
import com.viv.gunchung.ngawamovie.models.Movie;
import com.viv.gunchung.ngawamovie.models.MovieReview;
import com.viv.gunchung.ngawamovie.models.MovieVideo;
import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

public class DetailMovieActivity extends AppCompatActivity implements MoviesVideoAdapter.MoviesVideoAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<MovieVideo>> {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();

    private static final int VIDEO_LOADER_ID = 23;
    private static final String VIDEO_QUERY_URL_EXTRA = "queryVideo";

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private ImageView mBackdropImageView;
    private ProgressBar mVideoLoading;
    private ImageView mPlayButton;

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseYearTextView;

    private RecyclerView mVideoRecycleView;
    private MoviesVideoAdapter mVideoAdapter;

    private RecyclerView mReviewRecycleView;
    private MoviesReviewAdapter mReviewAdapter;

    private Movie mSelectedMovie = new Movie();
    private Boolean mIsFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        mToolbar = (Toolbar) findViewById(R.id.detail_movies_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mPosterImageView = (ImageView) findViewById(R.id.iv_detail_movies_poster);
        mBackdropImageView = (ImageView) findViewById(R.id.iv_detail_movies_backdrop);
        mTitleTextView = (TextView) findViewById(R.id.tv_detail_movies_title);
        mRatingTextView = (TextView) findViewById(R.id.tv_detail_movies_rating);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mReleaseYearTextView = (TextView) findViewById(R.id.tv_release_year);
        mVideoLoading = (ProgressBar) findViewById(R.id.pb_video_loading);
        mPlayButton = (ImageView) findViewById(R.id.iv_detail_movies_play);

        loadMovieData();

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setTitle(mSelectedMovie.getTitle());

        initializeFAB();

        mVideoRecycleView = (RecyclerView) findViewById(R.id.rv_detail_video);
        mVideoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mVideoAdapter = new MoviesVideoAdapter(this, this);
        mVideoRecycleView.setAdapter(mVideoAdapter);


        mReviewRecycleView = (RecyclerView) findViewById(R.id.rv_detail_review);
        mReviewRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new MoviesReviewAdapter();
        mReviewRecycleView.setAdapter(mReviewAdapter);

        if (savedInstanceState != null) {
            List<MovieReview> reviews = (List<MovieReview>) Parcels.unwrap(savedInstanceState.getParcelable("review"));
            mReviewAdapter.setReviewList(reviews);
        } else {
            String reviewUrl = MovieUtils.buildReviewListUrl(mSelectedMovie.getId());
            new FetchReviewListTask().execute(reviewUrl);
        }

        LoaderManager.LoaderCallbacks<List<MovieVideo>> callback = DetailMovieActivity.this;

        String defaultUrl = MovieUtils.buildVideoListUrl(mSelectedMovie.getId());
        Bundle queryBundle = new Bundle();
        queryBundle.putString(VIDEO_QUERY_URL_EXTRA, defaultUrl);

        getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, queryBundle, callback);
    }

    private void initializeFAB() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mIsFavorite = MovieUtils.isFavoriteMovie(DetailMovieActivity.this, mSelectedMovie.getId());

        if (mIsFavorite) {
            fab.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: start");

                if (mIsFavorite) {
                   // mark as not favorite
                    Uri uri = MovieContract.MovieEntry.buildMovieUriWithMovieId(mSelectedMovie.getId());
                    getContentResolver().delete(uri, null, null);
                    Snackbar.make(view, "Unmarked as favorite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    mIsFavorite = !mIsFavorite;

                } else {
                    // mark as favorite
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mSelectedMovie.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mSelectedMovie.getTitle());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mSelectedMovie.getVoteAverage());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mSelectedMovie.getPosterPath());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, mSelectedMovie.getBackdropPath());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mSelectedMovie.getOverview());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mSelectedMovie.getReleaseDate().getTime());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, mSelectedMovie.getPopularity());

                    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if(uri != null) {
                        Snackbar.make(view, "Marked as favorite", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                        mIsFavorite = !mIsFavorite;
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("review", Parcels.wrap(mReviewAdapter.getReviewList()));
    }

    @Override
    public Loader<List<MovieVideo>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<List<MovieVideo>>(this) {

            List<MovieVideo> mMovieVideo = null;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading Start");

                if (mMovieVideo != null) {
                    deliverResult(mMovieVideo);
                } else {
                    mVideoLoading.bringToFront();
                    mVideoLoading.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<MovieVideo> loadInBackground() {
                Log.d(TAG, "loadInBackground Start");

                String movieVideoUrl = loaderArgs.getString(VIDEO_QUERY_URL_EXTRA);

                Response videoResponse = null;
                try {
                    videoResponse = MovieUtils.getResponseFromHttpUrl(movieVideoUrl);
                } catch (IOException e) {
                    Log.e("Error loadInBackground", e.getMessage());
                }

                return MovieUtils.parseToVideoList(videoResponse);
            }

            @Override
            public void deliverResult(List<MovieVideo> data) {
                Log.d(TAG, "deliverResult Start");
                mMovieVideo = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> data) {
        Log.d(TAG, "onLoadFinished Start");
        mVideoLoading.setVisibility(View.INVISIBLE);
        Log.d("Size", "" + data.size());
        if (data.size() != 0) {
            mVideoAdapter.setVideoList(data);
            showMovieVideo();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieVideo>> loader) {
        // DO NOTHING
    }

    private void loadMovieData() {
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("MOVIE_ID")) {
                int id = intentThatStartedThisActivity.getIntExtra("MOVIE_ID", 0);
                mSelectedMovie.setId(id);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_TITLE")) {
                String title = intentThatStartedThisActivity.getStringExtra("MOVIE_TITLE");
                mTitleTextView.setText(title);
                mSelectedMovie.setTitle(title);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_VOTE_AVERAGE")) {
                double voteAverage = intentThatStartedThisActivity.getDoubleExtra("MOVIE_VOTE_AVERAGE", 0);
                mRatingTextView.setText(String.valueOf(voteAverage));
                mSelectedMovie.setVoteAverage(voteAverage);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_BACKDROP_PATH")) {
                String backdropPath = intentThatStartedThisActivity.getStringExtra("MOVIE_BACKDROP_PATH");
                String movieBackdropUrl = MovieUtils.generateImgUrl(backdropPath);

                mBackdropImageView.bringToFront();
                Picasso.with(this).load(movieBackdropUrl).into(mBackdropImageView);

                mSelectedMovie.setBackdropPath(backdropPath);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_POSTER_PATH")) {
                String posterPath = intentThatStartedThisActivity.getStringExtra("MOVIE_POSTER_PATH");
                String moviePosterUrl = MovieUtils.generateImgUrl(posterPath);

                mPosterImageView.bringToFront();
                Picasso.with(this).load(moviePosterUrl).into(mPosterImageView);

                mSelectedMovie.setPosterPath(posterPath);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_OVERVIEW")) {
                String overview = intentThatStartedThisActivity.getStringExtra("MOVIE_OVERVIEW");
                mOverviewTextView.setText(overview);
                mSelectedMovie.setOverview(overview);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_RELEASE_YEAR")) {
                int releaseYear = intentThatStartedThisActivity.getIntExtra("MOVIE_RELEASE_YEAR", 0);
                mReleaseYearTextView.setText(String.valueOf(releaseYear));
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_POPULARITY")) {
                Double popularity = intentThatStartedThisActivity.getDoubleExtra("MOVIE_POPULARITY", 0);
                mSelectedMovie.setPopularity(popularity);
            }

            if (intentThatStartedThisActivity.hasExtra("MOVIE_RELEASE_MILIS")) {
                long milis = intentThatStartedThisActivity.getLongExtra("MOVIE_RELEASE_MILIS", 0);
                Date releaseDate = new Date(milis);
                mSelectedMovie.setReleaseDate(releaseDate);
            }
        }
    }

    private void showMovieVideo() {
        mVideoLoading.setVisibility(View.INVISIBLE);

        mPlayButton.bringToFront();
        mPlayButton.setVisibility(View.VISIBLE);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFirstVideo();
            }
        });
    }

    private void showErrorMessage() {
        mPlayButton.setVisibility(View.INVISIBLE);
        mVideoLoading.setVisibility(View.INVISIBLE);
    }

    private void openFirstVideo() {
        String key = mVideoAdapter.getVideoList().get(0).getKey();
        String url = MovieUtils.generateYoutubeUrl(key);
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(MovieVideo selectedVideo) {
        String key = selectedVideo.getKey();
        String url = MovieUtils.generateYoutubeUrl(key);
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        mVideoAdapter.setVideoList(null);
        super.onDestroy();
    }


    /**
     * TODO: Refactor using better approach
     */
    private class FetchReviewListTask extends AsyncTask<String, Void, List<MovieReview>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieReview> doInBackground(String... params) {
            Log.d(TAG, "doInBackground Start");
            if (params.length == 0) {
                return null;
            }
            String reviewUrl = params[0];

            Response movieResponse = null;
            try {
                movieResponse = MovieUtils.getResponseFromHttpUrl(reviewUrl);
            } catch (IOException e) {
                Log.e("Error doInBackground", e.getMessage());
            }

            return MovieUtils.parseToReviewList(movieResponse);
        }

        @Override
        protected void onPostExecute(List<MovieReview> movieReviews) {
            Log.d("Size", "" + movieReviews.size());
            if (movieReviews.size() != 0) {
                mReviewAdapter.setReviewList(movieReviews);
            } else {
                showErrorMessage();
            }
        }
    }
}
