package com.viv.gunchung.ngawamovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gunawan on 05/08/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.viv.gunchung.ngawamovie";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static Uri buildMovieUriWithMovieId(int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(movieId))
                    .build();
        }

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE= "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }


}
