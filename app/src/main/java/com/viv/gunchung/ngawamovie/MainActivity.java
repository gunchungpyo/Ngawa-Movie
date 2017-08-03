package com.viv.gunchung.ngawamovie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viv.gunchung.ngawamovie.adapter.MoviesAdapter;
import com.viv.gunchung.ngawamovie.models.Movie;
import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 22;
    private static final String DISCOVER_STATE_EXTRA = "state";
    private static final String DISCOVER_POPULAR_STATE = "popular";
    private static final String DISCOVER_TOP_RATED_STATE = "top_rated";
    private static final String DISCOVER_FAVORITE_STATE = "favorite";

    private RecyclerView mMovieRecycleView;
    private MoviesAdapter mAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private String mDefaultState = DISCOVER_POPULAR_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMovieRecycleView = (RecyclerView) findViewById(R.id.rv_movies);
        setRecycleViewGridColumn();

        mAdapter = new MoviesAdapter(this, this);
        mMovieRecycleView.setAdapter(mAdapter);

        int loaderId = MOVIE_LOADER_ID;
        LoaderManager.LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(DISCOVER_STATE_EXTRA, mDefaultState);

        getSupportLoaderManager().initLoader(loaderId, queryBundle, callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            mDefaultState = DISCOVER_POPULAR_STATE;
            mAdapter.setMovieList(null);
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_top_rated) {
            mDefaultState = DISCOVER_TOP_RATED_STATE;
            mAdapter.setMovieList(null);
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_favorite) {
            mDefaultState = DISCOVER_FAVORITE_STATE;
            mAdapter.setMovieList(null);
            loadMoviesData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie selectedMovie) {

        // TODO: Using parceable

        Context context = this;
        Class destinationClass = DetailMovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra("MOVIE_ID", selectedMovie.getId());
        intentToStartDetailActivity.putExtra("MOVIE_TITLE", selectedMovie.getTitle());
        intentToStartDetailActivity.putExtra("MOVIE_VOTE_AVERAGE", selectedMovie.getVoteAverage());
        intentToStartDetailActivity.putExtra("MOVIE_POSTER_PATH", selectedMovie.getPosterPath());
        intentToStartDetailActivity.putExtra("MOVIE_BACKDROP_PATH", selectedMovie.getBackdropPath());
        intentToStartDetailActivity.putExtra("MOVIE_OVERVIEW", selectedMovie.getOverview());
        intentToStartDetailActivity.putExtra("MOVIE_POPULARITY", selectedMovie.getPopularity());

        long milis = selectedMovie.getReleaseDate().getTime();
        intentToStartDetailActivity.putExtra("MOVIE_RELEASE_MILIS", milis);

        int year = MovieUtils.getMovieYear(selectedMovie.getReleaseDate());
        intentToStartDetailActivity.putExtra("MOVIE_RELEASE_YEAR", year);

        startActivity(intentToStartDetailActivity);
    }

    private void loadMoviesData() {
        showMoviesDataView();

        Bundle queryBundle = new Bundle();
        queryBundle.putString(DISCOVER_STATE_EXTRA, mDefaultState);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this);
        }

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovieData = null;

            @Override
            protected void onStartLoading() {
                Log.d(TAG, "onStartLoading: Start");

                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                Log.d(TAG, "loadInBackground Start");

                List<Movie> movies = null;

                String state = loaderArgs.getString(DISCOVER_STATE_EXTRA);
                switch (state) {
                    case DISCOVER_FAVORITE_STATE:

                        Cursor movieCursor = MovieUtils.getFavoriteMovie(MainActivity.this);
                        movies = MovieUtils.parseFavoriteToMovieList(movieCursor);
                        break;

                    default: // for most popular and top rated request

                        String url = MovieUtils.buildDiscoverUrl(state);
                        Response movieResponse = null;

                        try {
                            movieResponse = MovieUtils.getResponseFromHttpUrl(url);
                        } catch (IOException e) {
                            Log.e("Error loadInBackground", e.getMessage());
                        }
                        movies = MovieUtils.parseToMovieList(movieResponse);
                        break;
                }

                return movies;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                Log.d(TAG, "deliverResult Start");
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        Log.d(TAG, "onLoadFinished Start");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Log.d("Size", "" + movies.size());
        if (movies.size() != 0) {
            showMoviesDataView();
            mAdapter.setMovieList(movies);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

    private void setRecycleViewGridColumn() {
        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mMovieRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
        } else{
            mMovieRecycleView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        mMovieRecycleView.setItemAnimator(new DefaultItemAnimator());
    }

    private void showMoviesDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieRecycleView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieRecycleView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }


}
