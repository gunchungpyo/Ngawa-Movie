package com.viv.gunchung.ngawamovie.utilities;

import android.net.Uri;
import android.util.Log;

import com.viv.gunchung.ngawamovie.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private static final String LANGUAGE = "en-US";

    final static String API_KEY_PARAM = "api_key";
    final static String LANGUAGE_PARAM = "language";
    final static String PAGE_PARAM = "page";

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_FILE_SIZE = "w500";

    public static String buildDiscoverUrl(boolean sortByPopularity) {
        String baseUrl = sortByPopularity ? POPULAR_BASE_URL : TOP_RATED_BASE_URL;

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE)
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();

        Log.d(TAG, "builtUri: " + builtUri.toString());
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
                String overview = object.getString("overview");
                Date releaseDate = dateFormatter(object.getString("release_date"), "yyyy-mm-dd");
                double popularity = object.getDouble("popularity");

                Movie m = new Movie(id, title, voteAverage, posterPath, overview, releaseDate, popularity);
                parsedMovieData.add(m);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return parsedMovieData;
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
        String url = BASE_IMAGE_URL + IMAGE_FILE_SIZE + "/" + imagePath;
        return url;
    }
}
