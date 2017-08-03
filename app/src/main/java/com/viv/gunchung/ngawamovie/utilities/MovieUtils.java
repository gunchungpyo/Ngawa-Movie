package com.viv.gunchung.ngawamovie.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.viv.gunchung.ngawamovie.data.MovieContract;
import com.viv.gunchung.ngawamovie.models.Movie;
import com.viv.gunchung.ngawamovie.models.MovieReview;
import com.viv.gunchung.ngawamovie.models.MovieVideo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Gunawan on 08/07/2017.
 */

public final class MovieUtils {
    private static final String TAG = MovieUtils.class.getSimpleName();

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String VIDEO_PATH_URL = "/videos";
    private static final String REVIEW_PATH_URL = "/reviews";
    private static final String LANGUAGE = "en-US";

    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";
    private final static String PAGE_PARAM = "page";

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_LARGE_SIZE = "w500";

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String YOUTUBE_IMG_BASE_URL = "https://img.youtube.com/vi";
    private static final String YOUTUBE_IMG_STANDART_PATH = "0.jpg";
    private static final String YOUTUBE_KEY_PARAM = "v";

    private static final String DISCOVER_POPULAR_STATE = "popular";

    public static String buildDiscoverUrl(String state) {
        String baseUrl = (state.equalsIgnoreCase(DISCOVER_POPULAR_STATE)) ? POPULAR_BASE_URL : TOP_RATED_BASE_URL;

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();

        Log.d(TAG, "buildDiscoverUrl: " + builtUri.toString());
        return builtUri.toString();
    }

    public static String buildVideoListUrl(int movieId) {
        String videoUrl = MOVIE_BASE_URL + '/' + movieId + VIDEO_PATH_URL;

        Uri builtUri = Uri.parse(videoUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        Log.d(TAG, "buildVideoListUrl: " + builtUri.toString());
        return builtUri.toString();
    }

    public static String buildReviewListUrl(int movieId) {
        String reviewUrl = MOVIE_BASE_URL + '/' + movieId + REVIEW_PATH_URL;

        Uri builtUri = Uri.parse(reviewUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        Log.d(TAG, "buildReviewListUrl: " + builtUri.toString());
        return builtUri.toString();
    }

    public static Response getResponseFromHttpUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

    public static Cursor getFavoriteMovie(Context context) {
        try {
            return context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_TIMESTAMP);

        } catch (Exception e) {
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isFavoriteMovie(Context context, int movieId) {
        Log.d(TAG, "isFavoriteMovie: Start");
        Cursor result = null;
        try {
             result = context.getContentResolver().query(MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId),
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_TIMESTAMP);
            Log.d(TAG, "isFavoriteMovie: " + MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId));

        } catch (Exception e) {
            Log.e(TAG, "Failed to isFavoriteMovie.");
            e.printStackTrace();
        }
        Log.d(TAG, "isFavoriteMovie End: " + result.getCount());
        return (result.getCount() > 0);
    }

    public static List<Movie> parseToMovieList(Response response) {
        Log.d(TAG, "parseToMovieList Start");
        List<Movie> parsedMovieData = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            JSONObject moviesObject = new JSONObject(jsonData);
            JSONArray moviesArray = moviesObject.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject object = moviesArray.getJSONObject(i);

                int id = object.getInt("id");
                String title = object.getString("title");
                double voteAverage = object.getDouble("vote_average");
                String posterPath = object.getString("poster_path");
                String backdropPath = object.getString("backdrop_path");
                String overview = object.getString("overview");
                Date releaseDate = dateFormatter(object.getString("release_date"), "yyyy-mm-dd");
                double popularity = object.getDouble("popularity");

                Movie m = new Movie(id, title, voteAverage, posterPath, backdropPath, overview, releaseDate, popularity);
                parsedMovieData.add(m);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return parsedMovieData;
    }

    public static List<Movie> parseFavoriteToMovieList(Cursor movieCursor) {
        Log.d(TAG, "parseFavoriteToMovieList: Start");

        List<Movie> parsedMovieData = new ArrayList<>();

        if(movieCursor == null) return parsedMovieData;

        int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int titleIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int voteAverageIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int posterPathIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int backdropPathIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        int overviewIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int popularityIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY);

        movieCursor.moveToFirst();
        while (!movieCursor.isAfterLast()) {

            int id = movieCursor.getInt(movieIdIndex);
            String title = movieCursor.getString(titleIndex);
            double voteAverage = movieCursor.getDouble(voteAverageIndex);
            String posterPath = movieCursor.getString(posterPathIndex);
            String backdropPath = movieCursor.getString(backdropPathIndex);
            String overview = movieCursor.getString(overviewIndex);
            Date releaseDate = new Date(movieCursor.getLong(releaseDateIndex));
            double popularity = movieCursor.getDouble(popularityIndex);

            Movie m = new Movie(id, title, voteAverage, posterPath, backdropPath, overview, releaseDate, popularity);
            parsedMovieData.add(m);

            movieCursor.moveToNext();
        }

        return parsedMovieData;
    }

    public static List<MovieVideo> parseToVideoList(Response response) {
        Log.d(TAG, "parseToVideoList Start");
        List<MovieVideo> parsedVideoData = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            JSONObject moviesObject = new JSONObject(jsonData);
            JSONArray moviesArray = moviesObject.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject object = moviesArray.getJSONObject(i);

                String id = object.getString("id");
                String key = object.getString("key");
                String name = object.getString("name");
                String site = object.getString("site");
                String size = object.getString("size");
                String type = object.getString("type");


                MovieVideo video = new MovieVideo(id, key, name, site, size, type);
                parsedVideoData.add(video);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return parsedVideoData;
    }

    public static List<MovieReview> parseToReviewList(Response response) {
        Log.d(TAG, "parseToReviewList Start");
        List<MovieReview> parsedReviewData = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            JSONObject moviesObject = new JSONObject(jsonData);
            JSONArray moviesArray = moviesObject.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject object = moviesArray.getJSONObject(i);

                String id = object.getString("id");
                String author = object.getString("author");
                String content = object.getString("content");
                String url = object.getString("url");


                MovieReview review = new MovieReview(id, author, content, url);
                parsedReviewData.add(review);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return parsedReviewData;
    }

    public static Date dateFormatter(String dateString, String pattern) {
        Date parsed;
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat(pattern);
            parsed = format.parse(dateString);
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
        return parsed;
    }

    public static String generateImgUrl(String imagePath) {
        String url = BASE_IMAGE_URL + IMAGE_LARGE_SIZE + "/" + imagePath;
        return url;
    }

    public static String generateYoutubeUrl(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_KEY_PARAM, key)
                .build();

        Log.d(TAG, "generateYoutubeUrl: " + builtUri.toString());
        return builtUri.toString();
    }

    public static String generateYoutubeThumbUrl(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_IMG_BASE_URL).buildUpon()
                .appendPath(key)
                .appendPath(YOUTUBE_IMG_STANDART_PATH)
                .build();

        Log.d(TAG, "generateYoutubeThumbUrl: " + builtUri.toString());
        return builtUri.toString();
    }

    public static int getMovieYear(Date releaseDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(releaseDate);
        int year = cal.get(Calendar.YEAR);
        return year;
    }
}
