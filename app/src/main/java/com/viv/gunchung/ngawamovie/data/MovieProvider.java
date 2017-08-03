package com.viv.gunchung.ngawamovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.viv.gunchung.ngawamovie.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static com.viv.gunchung.ngawamovie.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by gunawan on 05/08/17.
 */

public class MovieProvider extends ContentProvider {

    private static final String TAG = MovieProvider.class.getSimpleName();

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.viv.gunchung.ngawamovie/movie/ */
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);

        /*
         * This URI would look something like content://com.viv.gunchung.ngawamovie/movie/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_MOVIE is followed by ANY number,
         * that it should return the CODE_MOVIE_WITH_ID code
         */
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;

    }


    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the movie directory and write a default case
        switch (match) {
            // Query for the movie directory
            case CODE_MOVIE:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIE_WITH_ID:
                String where = COLUMN_MOVIE_ID + " = ? ";
                String normalizedMovieIdString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedMovieIdString};

                Log.d(TAG, "query: " + normalizedMovieIdString);
                Log.d(TAG, "query: " + selectionArguments);

                retCursor = db.query(TABLE_NAME,
                        projection,
                        where,
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the movie directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case CODE_MOVIE:
                // Insert new values into the database
                // Inserting values into movie table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted movie
        int movieDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case CODE_MOVIE_WITH_ID:
                // Get the movie ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                movieDeleted = db.delete(TABLE_NAME, "movie_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (movieDeleted != 0) {
            // A movie was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of movie deleted
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
