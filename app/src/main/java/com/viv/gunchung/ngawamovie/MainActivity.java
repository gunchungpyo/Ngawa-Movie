package com.viv.gunchung.ngawamovie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mMovieRecycleView;
    private MoviesAdapter mAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    private boolean mSortByPopularity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieRecycleView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mMovieRecycleView.setLayoutManager(mLayoutManager);
        mMovieRecycleView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mMovieRecycleView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MoviesAdapter(this, this);
        mMovieRecycleView.setAdapter(mAdapter);

        loadMoviesData();
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
            mSortByPopularity = true;
            mAdapter.setMovieList(null);
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_top_rated) {
            mSortByPopularity = false;
            mAdapter.setMovieList(null);
            loadMoviesData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Context context = this;
        Class destinationClass = DetailMovieActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra("MOVIE_TITLE", selectedMovie.getTitle());
        intentToStartDetailActivity.putExtra("MOVIE_VOTE_AVERAGE", selectedMovie.getVoteAverage());
        intentToStartDetailActivity.putExtra("MOVIE_POSTER_PATH", selectedMovie.getPosterPath());
        intentToStartDetailActivity.putExtra("MOVIE_OVERVIEW", selectedMovie.getOverview());

        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedMovie.getReleaseDate());
        int year = cal.get(Calendar.YEAR);
        intentToStartDetailActivity.putExtra("MOVIE_RELEASE_YEAR", year);

        startActivity(intentToStartDetailActivity);
    }

    private void loadMoviesData() {
        showMoviesDataView();

        String discoverUrl = MovieUtils.buildDiscoverUrl(mSortByPopularity);
        new FetchMovieListTask().execute(discoverUrl);
    }

    private void showMoviesDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieRecycleView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieRecycleView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            Log.d(TAG, "doInBackground Start");
            if (params.length == 0) {
                return null;
            }
            String discoverUrl = params[0];

            Response movieResponse = null;
            try {
                movieResponse = MovieUtils.getResponseFromHttpUrl(discoverUrl);
            } catch (IOException e) {
                Log.e("Error doInBackground", e.getMessage());
            }

            return MovieUtils.parseToMovieList(movieResponse);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            Log.d(TAG, "onPostExecute Start");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Log.d("Size", "" + movies.size());
            if (movies.size() != 0) {
                showMoviesDataView();
                mAdapter.setMovieList(movies);
            } else {
                showErrorMessage();
            }
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
